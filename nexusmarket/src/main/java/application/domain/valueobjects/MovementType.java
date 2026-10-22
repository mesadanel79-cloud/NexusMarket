package application.domain.valueobjects;

import java.util.List;

/**
 * Type of inventory movement.
 */
public final class MovementType extends DomainCatalog {
    public static final MovementType INFLOW = new MovementType(
            "INFLOW", "Inflow", "Stock received into a warehouse.");
    public static final MovementType RESERVATION = new MovementType(
            "RESERVATION", "Reservation", "Stock reserved for an order pending payment.");
    public static final MovementType RESERVATION_RELEASE = new MovementType(
            "RESERVATION_RELEASE", "Reservation Release", "Reserved stock returned to available after an order is cancelled or expires.");
    public static final MovementType SALE = new MovementType(
            "SALE", "Sale", "Reserved stock consumed by a paid order.");
    public static final MovementType ADJUSTMENT = new MovementType(
            "ADJUSTMENT", "Adjustment", "Manual correction after a physical count or units marked as damaged.");
    public static final MovementType RETURN = new MovementType(
            "RETURN", "Return", "Returned item received back into a warehouse (as available or damaged stock).");

    private static final List<MovementType> VALUES = List.of(
            INFLOW, RESERVATION, RESERVATION_RELEASE, SALE, ADJUSTMENT, RETURN);

    private MovementType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<MovementType> values() {
        return VALUES;
    }

    public static MovementType fromCode(String code) {
        return resolve(VALUES, code, "MovementType");
    }
}
