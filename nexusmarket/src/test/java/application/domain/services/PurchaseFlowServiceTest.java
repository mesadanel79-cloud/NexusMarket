package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.enums.ApprovalDecision;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidReturnException;
import application.domain.models.InventoryItem;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.services.order.CheckoutService;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.ReturnReason;
import application.domain.valueobjects.ReturnStatus;
import application.support.ServiceHarness;
import application.support.TestFixtures;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end business flow over the domain services: stock -> cart -> checkout ->
 * payment -> dispatch -> delivery -> return -> refund.
 */
class PurchaseFlowServiceTest {

    private ServiceHarness h;
    private TestFixtures f;

    @BeforeEach
    void setUp() {
        h = new ServiceHarness();
        f = new TestFixtures(h).scenario();
        h.inflow.registerInflow(f.operator, TestFixtures.warehouseRef("WH-MKT-01"), TestFixtures.skuRef("LAP-8GB"), 5);
        h.inflow.registerInflow(f.seller, TestFixtures.warehouseRef("WH-SEL-01"), TestFixtures.skuRef("LAP-8GB"), 2);
    }

    private static application.domain.models.CartItem item(String sku, int quantity) {
        application.domain.models.CartItem item = new application.domain.models.CartItem();
        item.setVariant(TestFixtures.skuRef(sku));
        item.setQuantity(quantity);
        return item;
    }

    private Order buyTwoLaptops() {
        h.cart.addItem(f.buyer, item("LAP-8GB", 2));
        return h.checkout.checkout(f.buyer, null);
    }

    @Test
    void checkoutReservesStockFromTheBestWarehouseAndEmptiesTheCart() {
        Order order = buyTwoLaptops();
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals("WH-MKT-01", order.getLines().get(0).getWarehouse().getIdentifier());
        InventoryItem item = h.inventory.store.get(InventoryItem.buildIdentifier("WH-MKT-01", "LAP-8GB"));
        assertEquals(3, item.getAvailableQuantity());
        assertEquals(2, item.getReservedQuantity());
        assertTrue(h.carts.store.get("1017000001").isEmpty());
    }

    @Test
    void cannotAddMoreThanTheAvailableStock() {
        assertThrows(InsufficientStockException.class, () -> h.cart.addItem(f.buyer,
                item("LAP-8GB", 8)));
    }

    @Test
    void completeFlowWithReturnAndRefund() {
        Order order = buyTwoLaptops();
        Order paid = h.pay.pay(f.buyer, order, PaymentMethod.CREDIT_CARD);
        assertEquals(OrderStatus.PAID, paid.getStatus());
        Invoice invoice = h.invoices.findByOrder(paid).orElseThrow();
        assertEquals(new BigDecimal("2000000.00"), invoice.getTotal().getAmount());
        assertEquals(new BigDecimal("1680672.27"), invoice.getSubtotal().getAmount());
        assertEquals(new BigDecimal("319327.73"), invoice.getTaxAmount().getAmount());

        Shipment shipment = h.dispatch.dispatch(f.operator, paid, "Servientrega", "TRK-001");
        assertEquals(OrderStatus.SHIPPED, h.orders.findByIdentifier(paid).orElseThrow().getStatus());
        h.deliver.confirm(f.operator, shipment);
        assertEquals(OrderStatus.DELIVERED, h.orders.findByIdentifier(paid).orElseThrow().getStatus());

        ReturnRequest request = new ReturnRequest();
        request.setOrder(paid);
        request.setSku("LAP-8GB");
        request.setQuantity(1);
        request.setReason(ReturnReason.DEFECTIVE);
        ReturnRequest created = h.requestReturn.request(f.buyer, request);
        h.reviewReturn.review(f.admin, created, ApprovalDecision.APPROVED, null);
        h.receiveReturn.receive(f.operator, created);
        ReturnRequest refunded = h.refund.issueRefund(f.admin, created);

        assertEquals(ReturnStatus.REFUNDED, refunded.getStatus());
        assertEquals(new BigDecimal("1000000.00"), refunded.getRefund().getAmount().getAmount());
        assertEquals(InvoiceStatus.PARTIALLY_REFUNDED, invoice.getStatus());
        InventoryItem item = h.inventory.store.get(InventoryItem.buildIdentifier("WH-MKT-01", "LAP-8GB"));
        assertEquals(1, item.getDamagedQuantity());
        assertEquals(3, item.getAvailableQuantity());
        assertTrue(h.inventory.movements.stream().anyMatch(m -> MovementType.SALE.equals(m.getMovementType())));
        assertTrue(h.operations.count(OperationType.REFUND_ISSUANCE) == 1);
        assertTrue(h.auditLogs.store.size() >= h.operations.store.size());
    }

    @Test
    void returnCannotExceedPurchasedQuantity() {
        Order order = buyTwoLaptops();
        h.pay.pay(f.buyer, order, PaymentMethod.DEBIT_CARD);
        Shipment shipment = h.dispatch.dispatch(f.operator, order, "Coordinadora", "TRK-2");
        h.deliver.confirm(f.operator, shipment);
        ReturnRequest request = new ReturnRequest();
        request.setOrder(order);
        request.setSku("LAP-8GB");
        request.setQuantity(3);
        request.setReason(ReturnReason.NO_LONGER_NEEDED);
        assertThrows(InvalidReturnException.class, () -> h.requestReturn.request(f.buyer, request));
    }

    @Test
    void digitalOrdersAreDeliveredImmediatelyAfterPayment() {
        h.cart.addItem(f.buyer, item("EBOOK-1", 1));
        Order order = h.checkout.checkout(f.buyer, null);
        Order paid = h.pay.pay(f.buyer, order, PaymentMethod.WALLET);
        assertEquals(OrderStatus.DELIVERED, paid.getStatus());
    }

    @Test
    void cancellingReleasesTheReservation() {
        Order order = buyTwoLaptops();
        h.cancel.cancel(f.buyer, order);
        InventoryItem item = h.inventory.store.get(InventoryItem.buildIdentifier("WH-MKT-01", "LAP-8GB"));
        assertEquals(5, item.getAvailableQuantity());
        assertEquals(0, item.getReservedQuantity());
    }

    @Test
    void unpaidOrdersExpireAndReleaseStock() {
        Order order = buyTwoLaptops();
        h.configuration.paymentExpirationMinutes = 0;
        assertEquals(1, h.expire.expireOverdueOrders().size());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(1, h.operations.count(OperationType.ORDER_EXPIRATION));
        assertEquals(CheckoutService.class, h.checkout.getClass());
    }
}
