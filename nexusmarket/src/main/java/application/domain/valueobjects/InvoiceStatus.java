package application.domain.valueobjects;

import java.util.List;

/**
 * Status of an invoice.
 */
public final class InvoiceStatus extends DomainCatalog {
    public static final InvoiceStatus ISSUED = new InvoiceStatus(
            "ISSUED", "Issued", "Invoice issued for a paid order.");
    public static final InvoiceStatus PARTIALLY_REFUNDED = new InvoiceStatus(
            "PARTIALLY_REFUNDED", "Partially Refunded", "Part of the invoice total has been refunded.");
    public static final InvoiceStatus REFUNDED = new InvoiceStatus(
            "REFUNDED", "Refunded", "The invoice total has been fully refunded.");

    private static final List<InvoiceStatus> VALUES = List.of(
            ISSUED, PARTIALLY_REFUNDED, REFUNDED);

    private InvoiceStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<InvoiceStatus> values() {
        return VALUES;
    }

    public static InvoiceStatus fromCode(String code) {
        return resolve(VALUES, code, "InvoiceStatus");
    }
}
