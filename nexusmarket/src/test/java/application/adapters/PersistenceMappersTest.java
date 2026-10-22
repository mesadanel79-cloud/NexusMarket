package application.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.mappers.AssetReferences;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseType;
import application.support.TestFixtures;
import org.junit.jupiter.api.Test;

/**
 * Domain <-> JPA entity round trips: catalog Value Objects are stored by code and
 * restored as the same singletons.
 */
class PersistenceMappersTest {

    @Test
    void productRoundTrip() {
        Seller seller = TestFixtures.newSellerUser("s", "900", "s@mail.com").getSeller();
        seller.register();
        Product product = TestFixtures.product("Phone", ProductType.PHYSICAL,
                TestFixtures.variant("PH-1", "10"), TestFixtures.variant("PH-2", "20"));
        product.setSeller(seller);
        product.publish();
        product.setIdentifier("PRD-1");
        ProductJpaEntity entity = ProductJpaMapper.toEntity(product);
        assertEquals("PUBLISHED", entity.getStatus());
        Product restored = ProductJpaMapper.toDomain(entity, seller);
        assertEquals(ProductStatus.PUBLISHED, restored.getStatus());
        assertEquals(2, restored.getVariants().size());
        assertEquals(product.requireVariant("PH-2").getPrice(), restored.requireVariant("PH-2").getPrice());
    }

    @Test
    void orderRoundTripKeepsLinesSnapshot() {
        Seller seller = TestFixtures.newSellerUser("s", "900", "s@mail.com").getSeller();
        seller.register();
        Product product = TestFixtures.product("Phone", ProductType.PHYSICAL, TestFixtures.variant("PH-1", "10"));
        product.setSeller(seller);
        product.publish();
        product.setIdentifier("PRD-1");
        Buyer buyer = TestFixtures.newBuyerUser("b", "111", "b@mail.com").getBuyer();
        buyer.register();
        Cart cart = Cart.openFor(buyer);
        cart.addItem(product, "PH-1", 3);
        Order order = Order.draftFrom(cart, null);
        order.setIdentifier("ORD-1");
        Warehouse warehouse = TestFixtures.warehouse("WH-1", WarehouseType.MARKETPLACE);
        warehouse.register();
        order.allocateLine(1, warehouse);
        order.submitForPayment();

        OrderJpaEntity entity = OrderJpaMapper.toEntity(order);
        assertEquals("ORD-1-1", entity.getLines().get(0).getOrderLineId());
        Order restored = OrderJpaMapper.toDomain(entity, buyer);
        assertEquals(order.getStatus(), restored.getStatus());
        assertEquals(order.getTotalAmount(), restored.getTotalAmount());
        assertEquals("WH-1", restored.getLines().get(0).getWarehouse().getIdentifier());
        assertEquals(true, restored.requiresShipping());
        assertEquals("ORDER", AssetReferences.typeOf(restored));
    }
}
