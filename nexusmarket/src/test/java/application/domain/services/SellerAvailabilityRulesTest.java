package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidOrderException;
import application.domain.models.CartItem;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SellerStatus;
import application.support.ServiceHarness;
import application.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Business rules about what a buyer can see and buy:
 * - a suspended seller cannot sell (catalog, cart and checkout agree);
 * - the cart validates stock with the same criterion used by the reservation at checkout.
 */
class SellerAvailabilityRulesTest {

    private ServiceHarness h;
    private TestFixtures f;

    @BeforeEach
    void setUp() {
        h = new ServiceHarness();
        f = new TestFixtures(h).scenario();
        // 3 units in the marketplace warehouse and 3 in the seller warehouse (6 in total).
        h.inflow.registerInflow(f.operator, TestFixtures.warehouseRef("WH-MKT-01"), TestFixtures.skuRef("LAP-8GB"), 3);
        h.inflow.registerInflow(f.seller, TestFixtures.warehouseRef("WH-SEL-01"), TestFixtures.skuRef("LAP-8GB"), 3);
    }

    private static CartItem item(String sku, int quantity) {
        CartItem item = new CartItem();
        item.setVariant(TestFixtures.skuRef(sku));
        item.setQuantity(quantity);
        return item;
    }

    private static Product ref(Product product) {
        Product probe = new Product();
        probe.setIdentifier(product.getIdentifier());
        return probe;
    }

    @Test
    void publicCatalogHidesProductsOfSuspendedSellers() {
        assertEquals(2, h.catalog.consultPublished(null).size());
        f.laptop.getSeller().changeStatus(SellerStatus.SUSPENDED);

        assertTrue(h.catalog.consultPublished(null).isEmpty());
        assertThrows(EntityNotFoundException.class, () -> h.catalog.consultPublishedProduct(ref(f.laptop)));
    }

    @Test
    void checkoutRejectsProductsWhoseSellerWasSuspendedAfterAddingToCart() {
        h.cart.addItem(f.buyer, item("LAP-8GB", 1));
        f.laptop.getSeller().changeStatus(SellerStatus.SUSPENDED);

        assertThrows(InvalidOrderException.class, () -> h.checkout.checkout(f.buyer, null));
    }

    @Test
    void reactivatedSellerCanSellAgain() {
        f.laptop.getSeller().changeStatus(SellerStatus.SUSPENDED);
        f.laptop.getSeller().changeStatus(SellerStatus.ACTIVE);
        assertTrue(f.laptop.isAvailableForSale());

        h.cart.addItem(f.buyer, item("LAP-8GB", 1));
        Order order = h.checkout.checkout(f.buyer, null);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }

    @Test
    void cartUsesTheSameStockCriterionAsTheCheckoutReservation() {
        // 6 units exist in total, but no single warehouse has 5: the cart must reject it
        // instead of accepting it and failing later at checkout.
        assertThrows(InsufficientStockException.class, () -> h.cart.addItem(f.buyer, item("LAP-8GB", 5)));

        h.cart.addItem(f.buyer, item("LAP-8GB", 3));
        Order order = h.checkout.checkout(f.buyer, null);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(3, order.getLines().get(0).getQuantity());
    }

    @Test
    void unpublishedProductIsNotAvailableForSale() {
        assertTrue(f.ebook.isAvailableForSale());
        f.ebook.getSeller().changeStatus(SellerStatus.SUSPENDED);
        assertFalse(f.ebook.isAvailableForSale());
    }
}
