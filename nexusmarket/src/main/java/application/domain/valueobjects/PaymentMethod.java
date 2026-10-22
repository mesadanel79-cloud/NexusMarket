package application.domain.valueobjects;

import java.util.List;

/**
 * Payment method used to pay an order.
 */
public final class PaymentMethod extends DomainCatalog {
    public static final PaymentMethod CREDIT_CARD = new PaymentMethod(
            "CREDIT_CARD", "Credit Card", "Payment with credit card.");
    public static final PaymentMethod DEBIT_CARD = new PaymentMethod(
            "DEBIT_CARD", "Debit Card", "Payment with debit card.");
    public static final PaymentMethod BANK_TRANSFER = new PaymentMethod(
            "BANK_TRANSFER", "Bank Transfer", "Payment through bank transfer (e.g. PSE).");
    public static final PaymentMethod WALLET = new PaymentMethod(
            "WALLET", "Wallet", "Payment with a digital wallet.");

    private static final List<PaymentMethod> VALUES = List.of(
            CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, WALLET);

    private PaymentMethod(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<PaymentMethod> values() {
        return VALUES;
    }

    public static PaymentMethod fromCode(String code) {
        return resolve(VALUES, code, "PaymentMethod");
    }
}
