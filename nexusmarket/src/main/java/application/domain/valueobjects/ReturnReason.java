package application.domain.valueobjects;

import java.util.List;

/**
 * Reason declared by the buyer for a return.
 */
public final class ReturnReason extends DomainCatalog {
    public static final ReturnReason DEFECTIVE = new ReturnReason(
            "DEFECTIVE", "Defective", "Product arrived damaged or does not work.");
    public static final ReturnReason WRONG_ITEM = new ReturnReason(
            "WRONG_ITEM", "Wrong Item", "A different product was delivered.");
    public static final ReturnReason NOT_AS_DESCRIBED = new ReturnReason(
            "NOT_AS_DESCRIBED", "Not As Described", "Product does not match its description.");
    public static final ReturnReason NO_LONGER_NEEDED = new ReturnReason(
            "NO_LONGER_NEEDED", "No Longer Needed", "Buyer changed their mind.");

    private static final List<ReturnReason> VALUES = List.of(
            DEFECTIVE, WRONG_ITEM, NOT_AS_DESCRIBED, NO_LONGER_NEEDED);

    private ReturnReason(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ReturnReason> values() {
        return VALUES;
    }

    public static ReturnReason fromCode(String code) {
        return resolve(VALUES, code, "ReturnReason");
    }
}
