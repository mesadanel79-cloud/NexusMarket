package application.domain.valueobjects;

import java.util.List;

/**
 * Operational status of a warehouse.
 */
public final class WarehouseStatus extends DomainCatalog {
    public static final WarehouseStatus ACTIVE = new WarehouseStatus(
            "ACTIVE", "Active", "Warehouse can receive and dispatch stock.");
    public static final WarehouseStatus INACTIVE = new WarehouseStatus(
            "INACTIVE", "Inactive", "Warehouse cannot receive nor dispatch stock.");

    private static final List<WarehouseStatus> VALUES = List.of(
            ACTIVE, INACTIVE);

    private WarehouseStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<WarehouseStatus> values() {
        return VALUES;
    }

    public static WarehouseStatus fromCode(String code) {
        return resolve(VALUES, code, "WarehouseStatus");
    }
}
