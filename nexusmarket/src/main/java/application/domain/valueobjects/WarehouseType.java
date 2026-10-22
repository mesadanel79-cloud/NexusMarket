package application.domain.valueobjects;

import java.util.List;

/**
 * Classification of a warehouse.
 */
public final class WarehouseType extends DomainCatalog {
    public static final WarehouseType MARKETPLACE = new WarehouseType(
            "MARKETPLACE", "Marketplace Warehouse", "Warehouse operated by NexusMarket; can store products of any seller.");
    public static final WarehouseType SELLER = new WarehouseType(
            "SELLER", "Seller Warehouse", "Warehouse owned by a seller; stores only that seller's products.");

    private static final List<WarehouseType> VALUES = List.of(
            MARKETPLACE, SELLER);

    private WarehouseType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<WarehouseType> values() {
        return VALUES;
    }

    public static WarehouseType fromCode(String code) {
        return resolve(VALUES, code, "WarehouseType");
    }
}
