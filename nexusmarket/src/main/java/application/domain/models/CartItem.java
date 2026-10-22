package application.domain.models;

import application.domain.exceptions.InvalidCartException;
import application.domain.valueobjects.Money;

/**
 * Line of a shopping cart: a product variant, the desired quantity and the unit
 * price captured when the item was added.
 */
public class CartItem {

    private Product product;
    private ProductVariant variant;
    private int quantity;
    private Money unitPrice;

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }

    void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidCartException("Cart item quantity must be greater than zero.");
        }
        this.quantity = newQuantity;
    }

    public String getSku() {
        return variant == null ? null : variant.getSku();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }
}
