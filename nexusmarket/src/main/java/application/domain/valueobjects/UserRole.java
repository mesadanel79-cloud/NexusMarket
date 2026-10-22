package application.domain.valueobjects;

import java.util.List;

/**
 * Single system role assigned to a user (one role per user).
 */
public final class UserRole extends DomainCatalog {
    public static final UserRole BUYER = new UserRole(
            "BUYER", "Buyer", "Customer that buys products in the marketplace.");
    public static final UserRole SELLER = new UserRole(
            "SELLER", "Seller", "Merchant that publishes and sells products. Registered only by an administrator.");
    public static final UserRole LOGISTIC_OPERATOR = new UserRole(
            "LOGISTIC_OPERATOR", "Logistic Operator", "Runs the physical operation of warehouses: stock, packing, dispatch and returned items.");
    public static final UserRole ADMIN = new UserRole(
            "ADMIN", "Administrator", "Administers users, buyers, sellers and warehouses; approves returns and issues refunds.");
    public static final UserRole SUPERVISOR = new UserRole(
            "SUPERVISOR", "Supervisor", "Read-only profile for operational follow-up, reports and audit.");

    private static final List<UserRole> VALUES = List.of(
            BUYER, SELLER, LOGISTIC_OPERATOR, ADMIN, SUPERVISOR);

    private UserRole(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<UserRole> values() {
        return VALUES;
    }

    public static UserRole fromCode(String code) {
        return resolve(VALUES, code, "UserRole");
    }
}
