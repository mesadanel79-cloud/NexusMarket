package application.domain.models;

import application.domain.exceptions.InvalidCartException;
import application.domain.valueobjects.Money;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shopping cart of a buyer (one active cart per buyer).
 */
public class Cart {

    private String cartId;
    private Buyer buyer;
    private List<CartItem> items = new ArrayList<>();
    private LocalDateTime updatedAt;

    public static String buildIdentifier(Buyer buyer) {
        return "CART-" + buyer.getIdentification();
    }

    public static Cart openFor(Buyer buyer) {
        if (buyer == null || buyer.getIdentification() == null) {
            throw new InvalidCartException("Cart buyer is required.");
        }
        Cart cart = new Cart();
        cart.cartId = buildIdentifier(buyer);
        cart.buyer = buyer;
        cart.updatedAt = LocalDateTime.now();
        return cart;
    }

    /**
     * Adds a purchasable variant. If the SKU is already in the cart the quantity is increased.
     */
    public void addItem(Product product, String sku, int quantity) {
        if (product == null) {
            throw new InvalidCartException("Product is required.");
        }
        if (!product.isPurchasable()) {
            throw new InvalidCartException("Product " + product.getIdentifier() + " is not available for purchase.");
        }
        if (product.getSeller() != null && !product.getSeller().isActive()) {
            throw new InvalidCartException("The seller of product " + product.getIdentifier() + " is not active.");
        }
        if (quantity <= 0) {
            throw new InvalidCartException("Quantity must be greater than zero.");
        }
        ProductVariant variant = product.requireVariant(sku);
        if (!product.isPhysical() && quantity > 1) {
            throw new InvalidCartException("Digital products can only be purchased one unit at a time.");
        }
        if (!items.isEmpty() && !items.get(0).getUnitPrice().hasSameCurrency(variant.getPrice())) {
            throw new InvalidCartException("All cart items must share the same currency.");
        }
        Optional<CartItem> existing = findItem(sku);
        if (existing.isPresent()) {
            int newQuantity = existing.get().getQuantity() + quantity;
            if (!product.isPhysical()) {
                throw new InvalidCartException("Digital product " + sku + " is already in the cart.");
            }
            existing.get().changeQuantity(newQuantity);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setVariant(variant);
            item.setQuantity(quantity);
            item.setUnitPrice(variant.getPrice());
            items.add(item);
        }
        touch();
    }

    public void updateQuantity(String sku, int quantity) {
        CartItem item = findItem(sku).orElseThrow(
                () -> new InvalidCartException("SKU " + sku + " is not in the cart."));
        if (item.getProduct() != null && !item.getProduct().isPhysical() && quantity > 1) {
            throw new InvalidCartException("Digital products can only be purchased one unit at a time.");
        }
        item.changeQuantity(quantity);
        touch();
    }

    public void removeItem(String sku) {
        CartItem item = findItem(sku).orElseThrow(
                () -> new InvalidCartException("SKU " + sku + " is not in the cart."));
        items.remove(item);
        touch();
    }

    public void clear() {
        items.clear();
        touch();
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public Optional<CartItem> findItem(String sku) {
        if (sku == null) {
            return Optional.empty();
        }
        return items.stream().filter(i -> sku.equals(i.getSku())).findFirst();
    }

    public Money getTotal() {
        if (isEmpty()) {
            return null;
        }
        Money total = Money.zero(items.get(0).getUnitPrice().getCurrency());
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
