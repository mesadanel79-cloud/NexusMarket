package application.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidInventoryOperationException;
import application.domain.exceptions.NegativeStockException;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseType;
import application.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryItemTest {

    private Warehouse warehouse;
    private Product product;

    @BeforeEach
    void setUp() {
        warehouse = TestFixtures.warehouse("WH-T-01", WarehouseType.MARKETPLACE);
        warehouse.register();
        Seller seller = TestFixtures.newSellerUser("s1", "900", "s1@mail.com").getSeller();
        seller.register();
        product = TestFixtures.product("Phone", ProductType.PHYSICAL, TestFixtures.variant("PHONE-1", "100"));
        product.setSeller(seller);
        product.publish();
    }

    @Test
    void fullLifecycleKeepsQuantitiesConsistent() {
        InventoryItem item = InventoryItem.open(warehouse, product, product.requireVariant("PHONE-1"));
        item.registerInflow(10);
        item.reserve(4);
        item.confirmSale(3);
        item.releaseReservation(1);
        item.markDamaged(2);
        item.registerReturn(1, false);
        assertEquals(6, item.getAvailableQuantity());
        assertEquals(0, item.getReservedQuantity());
        assertEquals(2, item.getDamagedQuantity());
    }

    @Test
    void neverAllowsNegativeStock() {
        InventoryItem item = InventoryItem.open(warehouse, product, product.requireVariant("PHONE-1"));
        item.registerInflow(2);
        assertThrows(InsufficientStockException.class, () -> item.reserve(3));
        assertThrows(NegativeStockException.class, () -> item.confirmSale(1));
        assertThrows(NegativeStockException.class, () -> item.adjustAvailableTo(-1));
        assertThrows(NegativeStockException.class, () -> item.markDamaged(5));
    }

    @Test
    void damagedUnitsCannotBeReserved() {
        InventoryItem item = InventoryItem.open(warehouse, product, product.requireVariant("PHONE-1"));
        item.registerInflow(3);
        item.markDamaged(3);
        assertThrows(InsufficientStockException.class, () -> item.reserve(1));
    }

    @Test
    void digitalProductsHaveNoInventory() {
        Product digital = TestFixtures.product("Ebook", ProductType.DIGITAL, TestFixtures.variant("EB-1", "10"));
        digital.setSeller(product.getSeller());
        digital.publish();
        assertThrows(InvalidInventoryOperationException.class,
                () -> InventoryItem.open(warehouse, digital, digital.requireVariant("EB-1")));
    }

    @Test
    void sellerWarehouseOnlyStoresItsOwnerProducts() {
        Seller other = TestFixtures.newSellerUser("s2", "901", "s2@mail.com").getSeller();
        other.register();
        Warehouse sellerWarehouse = TestFixtures.warehouse("WH-S2-01", WarehouseType.SELLER);
        sellerWarehouse.setOwner(other);
        sellerWarehouse.register();
        assertThrows(InvalidInventoryOperationException.class,
                () -> InventoryItem.open(sellerWarehouse, product, product.requireVariant("PHONE-1")));
    }
}
