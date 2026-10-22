package application.domain.models;

import application.domain.exceptions.InvalidSellerException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.SellerStatus;
import java.time.LocalDate;

/**
 * Merchant that publishes products in the marketplace. Sellers are registered only
 * by an administrator.
 */
public class Seller extends Person {

    private String storeName;
    private SellerStatus status;
    private LocalDate registrationDate;

    /**
     * Registers the seller establishing the initial {@code ACTIVE} status.
     */
    public void register() {
        if (status != null) {
            throw new InvalidSellerException("Seller is already registered.");
        }
        validateContactData();
        if (storeName == null || storeName.isBlank()) {
            throw new InvalidSellerException("Store name is required.");
        }
        if (storeName.length() > 100) {
            throw new InvalidSellerException("Store name must not exceed 100 characters.");
        }
        this.status = SellerStatus.ACTIVE;
        this.registrationDate = LocalDate.now();
    }

    public boolean isActive() {
        return SellerStatus.ACTIVE.equals(status);
    }

    /**
     * Changes the commercial status. Supported: ACTIVE -> SUSPENDED, SUSPENDED -> ACTIVE.
     */
    public void changeStatus(SellerStatus target) {
        if (target == null) {
            throw new InvalidSellerException("Target seller status must be provided.");
        }
        boolean valid = (SellerStatus.ACTIVE.equals(status) && SellerStatus.SUSPENDED.equals(target))
                || (SellerStatus.SUSPENDED.equals(status) && SellerStatus.ACTIVE.equals(target));
        if (!valid) {
            throw new InvalidStatusTransitionException("Seller",
                    status == null ? "UNKNOWN" : status.getCode(), target.getCode());
        }
        this.status = target;
    }

    public boolean sameAs(Seller other) {
        return other != null && getIdentification() != null
                && getIdentification().equals(other.getIdentification());
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public void setStatus(SellerStatus status) {
        this.status = status;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}
