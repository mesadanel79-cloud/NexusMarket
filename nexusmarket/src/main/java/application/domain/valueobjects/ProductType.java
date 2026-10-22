package application.domain.valueobjects;

import java.util.List;

/**
 * Nature of a product.
 */
public final class ProductType extends DomainCatalog {
    public static final ProductType PHYSICAL = new ProductType(
            "PHYSICAL", "Physical", "Tangible product that requires warehouse stock and shipping.");
    public static final ProductType DIGITAL = new ProductType(
            "DIGITAL", "Digital", "Downloadable or licensed product without stock nor shipping.");

    private static final List<ProductType> VALUES = List.of(
            PHYSICAL, DIGITAL);

    private ProductType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductType> values() {
        return VALUES;
    }

    public static ProductType fromCode(String code) {
        return resolve(VALUES, code, "ProductType");
    }
}
