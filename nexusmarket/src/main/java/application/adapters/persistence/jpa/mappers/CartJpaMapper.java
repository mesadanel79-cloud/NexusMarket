package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.amount;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.currency;

import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.persistence.jpa.entities.CartJpaEntity;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart mapping. Items are stored by SKU; when reading, the adapter resolves each SKU
 * to its current Product so that availability rules are evaluated on fresh data.
 */
public final class CartJpaMapper {

    private CartJpaMapper() {
    }

    public static CartJpaEntity toEntity(Cart domain) {
        CartJpaEntity entity = new CartJpaEntity();
        entity.setCartId(domain.getCartId());
        entity.setBuyerIdentification(domain.getBuyer().getIdentification());
        entity.setUpdatedAt(domain.getUpdatedAt());
        List<CartItemJpaEntity> items = new ArrayList<>();
        for (CartItem item : domain.getItems()) {
            CartItemJpaEntity itemEntity = new CartItemJpaEntity();
            itemEntity.setCartItemId(domain.getCartId() + "-" + item.getSku());
            itemEntity.setSku(item.getSku());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPriceAmount(amount(item.getUnitPrice()));
            itemEntity.setCurrency(currency(item.getUnitPrice()));
            items.add(itemEntity);
        }
        entity.setItems(items);
        return entity;
    }
}
