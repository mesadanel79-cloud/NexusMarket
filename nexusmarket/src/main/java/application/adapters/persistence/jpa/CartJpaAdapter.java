package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.persistence.jpa.entities.CartJpaEntity;
import application.adapters.persistence.jpa.mappers.CartJpaMapper;
import application.adapters.persistence.jpa.mappers.JpaMapperSupport;
import application.adapters.persistence.jpa.repositories.SpringDataCartRepository;
import application.adapters.persistence.jpa.repositories.SpringDataProductRepository;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.ports.out.CartRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Cart persistence. Items whose product no longer exists are dropped when reading.
 */
@Repository
public class CartJpaAdapter implements CartRepositoryPort {

    private final SpringDataCartRepository cartRepository;
    private final SpringDataProductRepository productRepository;
    private final ProductJpaAdapter productJpaAdapter;

    public CartJpaAdapter(SpringDataCartRepository cartRepository, SpringDataProductRepository productRepository,
                          ProductJpaAdapter productJpaAdapter) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productJpaAdapter = productJpaAdapter;
    }

    @Override
    public Cart save(Cart cart) {
        cartRepository.save(CartJpaMapper.toEntity(cart));
        return cart;
    }

    @Override
    public Optional<Cart> findByBuyer(Buyer buyer) {
        return cartRepository.findByBuyerIdentification(buyer.getIdentification())
                .map(entity -> toDomain(entity, buyer));
    }

    private Cart toDomain(CartJpaEntity entity, Buyer buyer) {
        Cart cart = new Cart();
        cart.setCartId(entity.getCartId());
        cart.setBuyer(buyer);
        cart.setUpdatedAt(entity.getUpdatedAt());
        List<CartItem> items = new ArrayList<>();
        for (CartItemJpaEntity itemEntity : entity.getItems()) {
            Optional<Product> product = productRepository.findByVariantsSku(itemEntity.getSku())
                    .map(productJpaAdapter::toDomain);
            if (product.isEmpty()) {
                continue;
            }
            CartItem item = new CartItem();
            item.setProduct(product.get());
            item.setVariant(product.get().requireVariant(itemEntity.getSku()));
            item.setQuantity(itemEntity.getQuantity());
            item.setUnitPrice(JpaMapperSupport.money(itemEntity.getUnitPriceAmount(), itemEntity.getCurrency()));
            items.add(item);
        }
        cart.setItems(items);
        return cart;
    }
}
