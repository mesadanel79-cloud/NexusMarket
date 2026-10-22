package application.domain.valueobjects;

import java.util.List;

/**
 * Status of a shipment.
 */
public final class ShipmentStatus extends DomainCatalog {
    public static final ShipmentStatus IN_TRANSIT = new ShipmentStatus(
            "IN_TRANSIT", "In Transit", "Shipment dispatched and travelling to the buyer.");
    public static final ShipmentStatus DELIVERED = new ShipmentStatus(
            "DELIVERED", "Delivered", "Shipment delivered to the buyer.");

    private static final List<ShipmentStatus> VALUES = List.of(
            IN_TRANSIT, DELIVERED);

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ShipmentStatus> values() {
        return VALUES;
    }

    public static ShipmentStatus fromCode(String code) {
        return resolve(VALUES, code, "ShipmentStatus");
    }
}
