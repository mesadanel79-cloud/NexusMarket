package application.domain.valueobjects;

import java.util.List;

/**
 * Commercial status of a seller.
 */
public final class SellerStatus extends DomainCatalog {
    public static final SellerStatus ACTIVE = new SellerStatus(
            "ACTIVE", "Active", "Seller can publish and sell products.");
    public static final SellerStatus SUSPENDED = new SellerStatus(
            "SUSPENDED", "Suspended", "Seller cannot publish products nor receive new sales.");

    private static final List<SellerStatus> VALUES = List.of(
            ACTIVE, SUSPENDED);

    private SellerStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<SellerStatus> values() {
        return VALUES;
    }

    public static SellerStatus fromCode(String code) {
        return resolve(VALUES, code, "SellerStatus");
    }
}
