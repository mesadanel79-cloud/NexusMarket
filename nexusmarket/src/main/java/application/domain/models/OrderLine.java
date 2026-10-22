package application.domain.models;

import application.domain.valueobjects.Money;

/**
 * Line of an order. Captures product, variant, seller, quantity and unit price at
 * checkout time, and the warehouse from which the stock was reserved (physical only).
 */
public class OrderLine {

    private int lineNumber;
    private Product product;
    private ProductVariant variant;
    private Seller seller;
    private int quantity;
    private Money unitPrice;
    private Warehouse warehouse;

    public Money getLineTotal() {
        return unitPrice.multiply(quantity);
    }

    public boolean isPhysical() {
        return product != null && product.isPhysical();
    }

    public String getSku() {
        return variant == null ? null : variant.getSku();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
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

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
