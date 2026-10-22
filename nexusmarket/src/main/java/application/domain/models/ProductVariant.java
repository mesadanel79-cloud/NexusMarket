package application.domain.models;

import application.domain.exceptions.InvalidProductException;
import application.domain.valueobjects.Money;

/**
 * Purchasable variant of a product (for example a color/size combination or a
 * license edition). Identified globally by its SKU.
 */
public class ProductVariant {

    private String sku;
    private String description;
    private Money price;

    public void validate() {
        if (sku == null || !sku.matches("^[A-Z0-9-]{3,40}$")) {
            throw new InvalidProductException(
                    "Variant SKU must have 3 to 40 characters (uppercase letters, digits or '-').");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidProductException("Variant description is required.");
        }
        if (price == null || !price.isPositive()) {
            throw new InvalidProductException("Variant price must be greater than zero.");
        }
    }

    public void changePrice(Money newPrice) {
        if (newPrice == null || !newPrice.isPositive()) {
            throw new InvalidProductException("Variant price must be greater than zero.");
        }
        if (price != null && !price.hasSameCurrency(newPrice)) {
            throw new InvalidProductException("Variant price currency cannot change.");
        }
        this.price = newPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }
}
