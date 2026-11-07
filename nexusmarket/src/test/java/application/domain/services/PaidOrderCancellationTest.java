package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.OrderImmutableException;
import application.domain.models.CartItem;
import application.domain.models.InventoryItem;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.support.ServiceHarness;
import application.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Cancellation of a PAID order before shipping: the money is refunded (invoice REFUNDED)
 * and the sold units return to available stock. Shipped or delivered orders cannot be
 * cancelled (after delivery the buyer uses the return process).
 */
class PaidOrderCancellationTest {

    private ServiceHarness h;
    private TestFixtures f;

    @BeforeEach
    void setUp() {
        h = new ServiceHarness();
        f = new TestFixtures(h).scenario();
        h.inflow.registerInflow(f.operator, TestFixtures.warehouseRef("WH-MKT-01"), TestFixtures.skuRef("LAP-8GB"), 5);
    }

    private Order paidOrderOfTwoLaptops() {
        CartItem item = new CartItem();
        item.setVariant(TestFixtures.skuRef("LAP-8GB"));
        item.setQuantity(2);
        h.cart.addItem(f.buyer, item);
        Order order = h.checkout.checkout(f.buyer, null);
        return h.pay.pay(f.buyer, order, PaymentMethod.CREDIT_CARD);
    }

    private InventoryItem laptopStock() {
        return h.inventory.store.get(InventoryItem.buildIdentifier("WH-MKT-01", "LAP-8GB"));
    }

    @Test
    void cancellingAPaidOrderRefundsTheMoneyAndReturnsTheStock() {
        Order paid = paidOrderOfTwoLaptops();
        assertEquals(3, laptopStock().getAvailableQuantity());

        Order cancelled = h.cancel.cancel(f.buyer, paid);

        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        // The units never left the warehouse: they are available again.
        assertEquals(5, laptopStock().getAvailableQuantity());
        assertEquals(0, laptopStock().getReservedQuantity());
        assertTrue(h.inventory.movements.stream().anyMatch(m -> MovementType.RETURN.equals(m.getMovementType())));
        // The whole invoice is refunded.
        Invoice invoice = h.invoices.findByOrder(paid).orElseThrow();
        assertEquals(InvoiceStatus.REFUNDED, invoice.getStatus());
        assertEquals(invoice.getTotal(), invoice.getRefundedAmount());
        assertEquals(1, h.operations.count(OperationType.REFUND_ISSUANCE));
        assertEquals(1, h.operations.count(OperationType.ORDER_CANCELLATION));
    }

    @Test
    void aCancelledPaidOrderCannotBeCancelledOrModifiedAgain() {
        Order paid = paidOrderOfTwoLaptops();
        h.cancel.cancel(f.buyer, paid);
        assertThrows(OrderImmutableException.class, () -> h.cancel.cancel(f.buyer, paid));
    }

    @Test
    void shippedOrdersCannotBeCancelled() {
        Order paid = paidOrderOfTwoLaptops();
        h.dispatch.dispatch(f.operator, paid, "Servientrega", "TRK-CANCEL");
        assertThrows(InvalidStatusTransitionException.class, () -> h.cancel.cancel(f.buyer, paid));
        assertEquals(3, laptopStock().getAvailableQuantity());
    }

    @Test
    void deliveredDigitalOrdersCannotBeCancelled() {
        CartItem item = new CartItem();
        item.setVariant(TestFixtures.skuRef("EBOOK-1"));
        item.setQuantity(1);
        h.cart.addItem(f.buyer, item);
        Order order = h.checkout.checkout(f.buyer, null);
        Order delivered = h.pay.pay(f.buyer, order, PaymentMethod.WALLET);
        assertThrows(OrderImmutableException.class, () -> h.cancel.cancel(f.buyer, delivered));
    }
}
