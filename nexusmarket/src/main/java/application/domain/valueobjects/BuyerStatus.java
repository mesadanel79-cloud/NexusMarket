package application.domain.valueobjects;

import java.util.List;

/**
 * Commercial status of a buyer.
 */
public final class BuyerStatus extends DomainCatalog {
    public static final BuyerStatus ACTIVE = new BuyerStatus(
            "ACTIVE", "Active", "Buyer can place orders.");
    public static final BuyerStatus SUSPENDED = new BuyerStatus(
            "SUSPENDED", "Suspended", "Buyer is temporarily not allowed to place orders.");

    private static final List<BuyerStatus> VALUES = List.of(
            ACTIVE, SUSPENDED);

    private BuyerStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<BuyerStatus> values() {
        return VALUES;
    }

    public static BuyerStatus fromCode(String code) {
        return resolve(VALUES, code, "BuyerStatus");
    }
}
