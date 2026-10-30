package application.domain.models;

import application.domain.exceptions.InvalidProductException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductCategory;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Catalog product published by a seller. Aggregate root of its variants.
 *
 * <p>Lifecycle: created as {@code PUBLISHED}; {@code PUBLISHED <-> SUSPENDED};
 * {@code PUBLISHED | SUSPENDED -> DISCONTINUED} (final).
 */
public class Product extends MarketplaceAsset {

    private Seller seller;
    private String name;
    private String description;
    private ProductCategory category;
    private ProductType productType;
    private ProductStatus status;
    private List<ProductVariant> variants = new ArrayList<>();
    private LocalDateTime createdAt;

    /**
     * Validates the new product and publishes it in the catalog.
     */
    public void publish() {
        if (status != null) {
            throw new InvalidProductException("Product is already registered.");
        }
        if (seller == null) {
            throw new InvalidProductException("Product seller is required.");
        }
        if (name == null || name.isBlank() || name.length() > 150) {
            throw new InvalidProductException("Product name is required (max 150 characters).");
        }
        if (category == null) {
            throw new InvalidProductException("Product category is required.");
        }
        if (productType == null) {
            throw new InvalidProductException("Product type is required.");
        }
        if (variants == null || variants.isEmpty()) {
            throw new InvalidProductException("A product requires at least one variant.");
        }
        List<ProductVariant> initial = new ArrayList<>(variants);
        variants = new ArrayList<>();
        for (ProductVariant variant : initial) {
            appendVariant(variant);
        }
        this.status = ProductStatus.PUBLISHED;
        this.createdAt = LocalDateTime.now();
    }

    public void addVariant(ProductVariant variant) {
        if (ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidProductException("Variants cannot be added to a discontinued product.");
        }
        appendVariant(variant);
    }

    private void appendVariant(ProductVariant variant) {
        if (variant == null) {
            throw new InvalidProductException("Variant must be provided.");
        }
        variant.validate();
        if (findVariant(variant.getSku()).isPresent()) {
            throw new InvalidProductException("Variant SKU " + variant.getSku() + " is duplicated in the product.");
        }
        if (!variants.isEmpty() && !variants.get(0).getPrice().hasSameCurrency(variant.getPrice())) {
            throw new InvalidProductException("All variants of a product must share the same currency.");
        }
        variants.add(variant);
    }

    public void changeVariantPrice(String sku, Money newPrice) {
        if (ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidProductException("Prices of a discontinued product cannot change.");
        }
        requireVariant(sku).changePrice(newPrice);
    }

    public void suspend() {
        if (!ProductStatus.PUBLISHED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", code(), ProductStatus.SUSPENDED.getCode());
        }
        this.status = ProductStatus.SUSPENDED;
    }

    public void reactivate() {
        if (!ProductStatus.SUSPENDED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", code(), ProductStatus.PUBLISHED.getCode());
        }
        this.status = ProductStatus.PUBLISHED;
    }

    public void discontinue() {
        if (!ProductStatus.PUBLISHED.equals(status) && !ProductStatus.SUSPENDED.equals(status)) {
            throw new InvalidStatusTransitionException("Product", code(), ProductStatus.DISCONTINUED.getCode());
        }
        this.status = ProductStatus.DISCONTINUED;
    }

    public boolean isPurchasable() {
        return ProductStatus.PUBLISHED.equals(status);
    }

    /**
     * Business rule: a product can be seen in the public catalog and bought only when it is
     * PUBLISHED and its seller is ACTIVE (a suspended seller cannot sell).
     */
    public boolean isAvailableForSale() {
        return isPurchasable() && seller != null && seller.isActive();
    }

    public boolean isPhysical() {
        return ProductType.PHYSICAL.equals(productType);
    }

    public boolean isOwnedBy(Seller candidate) {
        return seller != null && seller.sameAs(candidate);
    }

    public Optional<ProductVariant> findVariant(String sku) {
        if (sku == null || variants == null) {
            return Optional.empty();
        }
        return variants.stream().filter(v -> sku.equals(v.getSku())).findFirst();
    }

    public ProductVariant requireVariant(String sku) {
        return findVariant(sku).orElseThrow(
                () -> new InvalidProductException("Variant " + sku + " does not belong to product " + getIdentifier() + "."));
    }

    private String code() {
        return status == null ? "UNKNOWN" : status.getCode();
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
