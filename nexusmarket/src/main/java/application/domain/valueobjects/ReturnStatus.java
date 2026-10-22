package application.domain.valueobjects;

import java.util.List;

/**
 * Lifecycle status of a return request.
 */
public final class ReturnStatus extends DomainCatalog {
    public static final ReturnStatus REQUESTED = new ReturnStatus(
            "REQUESTED", "Requested", "Return requested by the buyer.");
    public static final ReturnStatus APPROVED = new ReturnStatus(
            "APPROVED", "Approved", "Return approved by an administrator.");
    public static final ReturnStatus REJECTED = new ReturnStatus(
            "REJECTED", "Rejected", "Return rejected by an administrator. Final.");
    public static final ReturnStatus RECEIVED = new ReturnStatus(
            "RECEIVED", "Received", "Returned item received in the warehouse.");
    public static final ReturnStatus REFUNDED = new ReturnStatus(
            "REFUNDED", "Refunded", "Refund issued to the buyer. Final.");

    private static final List<ReturnStatus> VALUES = List.of(
            REQUESTED, APPROVED, REJECTED, RECEIVED, REFUNDED);

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ReturnStatus> values() {
        return VALUES;
    }

    public static ReturnStatus fromCode(String code) {
        return resolve(VALUES, code, "ReturnStatus");
    }
}
