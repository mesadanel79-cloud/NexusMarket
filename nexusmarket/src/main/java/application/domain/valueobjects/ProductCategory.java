package application.domain.valueobjects;

import java.util.List;

/**
 * Catalog category of a product.
 */
public final class ProductCategory extends DomainCatalog {
    public static final ProductCategory ELECTRONICS = new ProductCategory(
            "ELECTRONICS", "Electronics", "Electronic devices and accessories.");
    public static final ProductCategory FASHION = new ProductCategory(
            "FASHION", "Fashion", "Clothing, shoes and accessories.");
    public static final ProductCategory HOME = new ProductCategory(
            "HOME", "Home", "Home and kitchen products.");
    public static final ProductCategory BOOKS = new ProductCategory(
            "BOOKS", "Books", "Printed and digital books.");
    public static final ProductCategory SPORTS = new ProductCategory(
            "SPORTS", "Sports", "Sports and outdoor products.");
    public static final ProductCategory SOFTWARE = new ProductCategory(
            "SOFTWARE", "Software", "Software licenses and digital tools.");
    public static final ProductCategory OTHER = new ProductCategory(
            "OTHER", "Other", "Products without a specific category.");

    private static final List<ProductCategory> VALUES = List.of(
            ELECTRONICS, FASHION, HOME, BOOKS, SPORTS, SOFTWARE, OTHER);

    private ProductCategory(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductCategory> values() {
        return VALUES;
    }

    public static ProductCategory fromCode(String code) {
        return resolve(VALUES, code, "ProductCategory");
    }
}
