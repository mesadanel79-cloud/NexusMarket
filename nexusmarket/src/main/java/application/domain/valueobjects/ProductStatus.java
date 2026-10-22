package application.domain.valueobjects;

import java.util.List;

/**
 * Catalog lifecycle status of a product.
 */
public final class ProductStatus extends DomainCatalog {
    public static final ProductStatus PUBLISHED = new ProductStatus(
            "PUBLISHED", "Published", "Product is visible and can be purchased.");
    public static final ProductStatus SUSPENDED = new ProductStatus(
            "SUSPENDED", "Suspended", "Product is temporarily hidden and cannot be purchased.");
    public static final ProductStatus DISCONTINUED = new ProductStatus(
            "DISCONTINUED", "Discontinued", "Product has been permanently withdrawn from the catalog.");

    private static final List<ProductStatus> VALUES = List.of(
            PUBLISHED, SUSPENDED, DISCONTINUED);

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductStatus> values() {
        return VALUES;
    }

    public static ProductStatus fromCode(String code) {
        return resolve(VALUES, code, "ProductStatus");
    }
}
