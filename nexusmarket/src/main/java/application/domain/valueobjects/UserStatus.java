package application.domain.valueobjects;

import java.util.List;

/**
 * Access status of a system user.
 */
public final class UserStatus extends DomainCatalog {
    public static final UserStatus ACTIVE = new UserStatus(
            "ACTIVE", "Active", "User can access the platform.");
    public static final UserStatus BLOCKED = new UserStatus(
            "BLOCKED", "Blocked", "User access has been suspended.");

    private static final List<UserStatus> VALUES = List.of(
            ACTIVE, BLOCKED);

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<UserStatus> values() {
        return VALUES;
    }

    public static UserStatus fromCode(String code) {
        return resolve(VALUES, code, "UserStatus");
    }
}
