package application.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.OrderImmutableException;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseType;
import application.support.TestFixtures;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {

    private Cart cart;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        Seller seller = TestFixtures.newSellerUser("s1", "900", "s1@mail.com").getSeller();
        seller.register();
        Product product = TestFixtures.product("Phone", ProductType.PHYSICAL, TestFixtures.variant("PHONE-1", "100"));
        product.setSeller(seller);
        product.publish();
        Buyer buyer = TestFixtures.newBuyerUser("b1", "111", "b1@mail.com").getBuyer();
        buyer.register();
        cart = Cart.openFor(buyer);
        cart.addItem(product, "PHONE-1", 2);
        warehouse = TestFixtures.warehouse("WH-1", WarehouseType.MARKETPLACE);
        warehouse.register();
    }

    @Test
    void followsTheStatusCycleOfTheSpecification() {
        Order order = Order.draftFrom(cart, null);
        assertEquals(OrderStatus.CART, order.getStatus());
        assertEquals(new BigDecimal("200.00"), order.getTotalAmount().getAmount());
        order.allocateLine(1, warehouse);
        order.submitForPayment();
        order.markPaid(PaymentMethod.CREDIT_CARD);
        order.markShipped();
        order.markDelivered();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void finalizedOrdersAreImmutable() {
        Order order = Order.draftFrom(cart, null);
        order.cancel();
        assertThrows(OrderImmutableException.class, () -> order.markPaid(PaymentMethod.WALLET));
        assertThrows(OrderImmutableException.class, order::cancel);
    }

    @Test
    void physicalLinesMustBeReservedBeforePayment() {
        Order order = Order.draftFrom(cart, null);
        assertThrows(InvalidOrderException.class, order::submitForPayment);
    }

    @Test
    void cannotSkipStates() {
        Order order = Order.draftFrom(cart, null);
        assertThrows(InvalidStatusTransitionException.class, order::markShipped);
    }

    @Test
    void shippingAddressMustBelongToTheBuyer() {
        assertThrows(InvalidOrderException.class, () -> Order.draftFrom(cart, "Somewhere else"));
        assertEquals("Office street 2", Order.draftFrom(cart, "Office street 2").getShippingAddress());
    }
}
