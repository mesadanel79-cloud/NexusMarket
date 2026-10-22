package application.domain.models;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidUserException;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import java.util.Arrays;

/**
 * System identity used for authentication and authorization.
 *
 * <p>Every user has exactly one {@link UserRole}. A BUYER user is linked to its
 * {@link Buyer} profile and a SELLER user to its {@link Seller} profile; staff users
 * (LOGISTIC_OPERATOR, SUPERVISOR, ADMIN) have no commercial profile.
 */
public class User extends Person {

    private Integer userId;
    private String username;
    private String password;
    private UserRole role;
    private UserStatus status;
    private Buyer buyer;
    private Seller seller;
    /** Version bound to issued JWTs ({@code ver} claim). Bumped on logout, password or status change. */
    private Integer authTokenVersion = 1;

    /**
     * Validates the registration data and activates the user.
     */
    public void activateOnRegistration() {
        if (status != null) {
            throw new InvalidUserException("User is already registered.");
        }
        validateContactData();
        requireText(username, "username");
        requireText(password, "password");
        if (username.length() < 4 || username.length() > 50) {
            throw new InvalidUserException("Username must have between 4 and 50 characters.");
        }
        if (role == null) {
            throw new InvalidUserException("User role is required.");
        }
        validateProfileForRole();
        this.status = UserStatus.ACTIVE;
        this.authTokenVersion = 1;
    }

    private void validateProfileForRole() {
        if (UserRole.BUYER.equals(role) && (buyer == null || seller != null)) {
            throw new InvalidUserException("A BUYER user must be linked only to a buyer profile.");
        }
        if (UserRole.SELLER.equals(role) && (seller == null || buyer != null)) {
            throw new InvalidUserException("A SELLER user must be linked only to a seller profile.");
        }
        if (isStaff() && (buyer != null || seller != null)) {
            throw new InvalidUserException("Staff users cannot be linked to buyer or seller profiles.");
        }
    }

    public boolean isStaff() {
        return UserRole.LOGISTIC_OPERATOR.equals(role)
                || UserRole.SUPERVISOR.equals(role)
                || UserRole.ADMIN.equals(role);
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status);
    }

    public boolean hasAnyRole(UserRole... roles) {
        return role != null && Arrays.asList(roles).contains(role);
    }

    /**
     * Changes the access status. Supported: ACTIVE -> BLOCKED, BLOCKED -> ACTIVE.
     * Invalidates previously issued tokens.
     */
    public void changeStatus(UserStatus target) {
        if (target == null) {
            throw new InvalidUserException("Target user status must be provided.");
        }
        boolean valid = (UserStatus.ACTIVE.equals(status) && UserStatus.BLOCKED.equals(target))
                || (UserStatus.BLOCKED.equals(status) && UserStatus.ACTIVE.equals(target));
        if (!valid) {
            throw new InvalidStatusTransitionException("User",
                    status == null ? "UNKNOWN" : status.getCode(), target.getCode());
        }
        this.status = target;
        bumpAuthTokenVersion();
    }

    /**
     * Replaces the password with an already encoded value and invalidates previous tokens.
     */
    public void changePassword(String encodedPassword) {
        requireText(encodedPassword, "password");
        this.password = encodedPassword;
        bumpAuthTokenVersion();
    }

    /**
     * Keeps the user's contact data aligned with its commercial profile.
     */
    public void syncContactData(Person source) {
        copyContactDataFrom(source);
    }

    public void bumpAuthTokenVersion() {
        this.authTokenVersion = authTokenVersion == null ? 2 : authTokenVersion + 1;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserException("Field '" + field + "' is required.");
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Integer getAuthTokenVersion() {
        return authTokenVersion;
    }

    public void setAuthTokenVersion(Integer authTokenVersion) {
        this.authTokenVersion = authTokenVersion;
    }
}
