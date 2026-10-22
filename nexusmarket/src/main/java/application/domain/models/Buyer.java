package application.domain.models;

import application.domain.exceptions.InvalidBuyerException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.BuyerStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Buyer of the marketplace.
 *
 * <p>Holds the main delivery address ({@code address}, inherited from Person), the
 * additional delivery addresses and the commercial status that decides whether the
 * buyer may place orders. A buyer never manages data of other buyers nor inventory.
 */
public class Buyer extends Person {

    private static final int MAX_ADDITIONAL_ADDRESSES = 5;

    private List<String> additionalAddresses = new ArrayList<>();
    private BuyerStatus status;

    /**
     * Registers the buyer establishing the initial {@code ACTIVE} commercial status.
     */
    public void register() {
        if (status != null) {
            throw new InvalidBuyerException("Buyer is already registered.");
        }
        validateContactData();
        validateAdditionalAddresses(additionalAddresses);
        this.status = BuyerStatus.ACTIVE;
    }

    public boolean canPurchase() {
        return BuyerStatus.ACTIVE.equals(status);
    }

    /**
     * A delivery address is valid when it is the main address or one of the additional ones.
     */
    public boolean isRegisteredAddress(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }
        String normalized = candidate.trim();
        return normalized.equalsIgnoreCase(getAddress() == null ? "" : getAddress().trim())
                || additionalAddresses.stream().anyMatch(a -> a.trim().equalsIgnoreCase(normalized));
    }

    /**
     * Changes the commercial status. Supported: ACTIVE -> SUSPENDED, SUSPENDED -> ACTIVE.
     */
    public void changeStatus(BuyerStatus target) {
        if (target == null) {
            throw new InvalidBuyerException("Target buyer status must be provided.");
        }
        boolean valid = (BuyerStatus.ACTIVE.equals(status) && BuyerStatus.SUSPENDED.equals(target))
                || (BuyerStatus.SUSPENDED.equals(status) && BuyerStatus.ACTIVE.equals(target));
        if (!valid) {
            throw new InvalidStatusTransitionException("Buyer",
                    status == null ? "UNKNOWN" : status.getCode(), target.getCode());
        }
        this.status = target;
    }

    /**
     * Updates contact data and additional addresses. Identification and status are immutable here.
     */
    public void updateProfile(Buyer desired) {
        List<String> newAddresses = desired.getAdditionalAddresses() == null
                ? new ArrayList<>() : new ArrayList<>(desired.getAdditionalAddresses());
        validateAdditionalAddresses(newAddresses);
        copyContactDataFrom(desired);
        this.additionalAddresses = newAddresses;
    }

    private static void validateAdditionalAddresses(List<String> addresses) {
        if (addresses == null) {
            return;
        }
        if (addresses.size() > MAX_ADDITIONAL_ADDRESSES) {
            throw new InvalidBuyerException("A buyer can register up to " + MAX_ADDITIONAL_ADDRESSES
                    + " additional addresses.");
        }
        for (String address : addresses) {
            if (address == null || address.isBlank() || address.length() > 200) {
                throw new InvalidBuyerException("Additional addresses must be non-empty (max 200 characters).");
            }
        }
    }

    public List<String> getAdditionalAddresses() {
        return additionalAddresses;
    }

    public void setAdditionalAddresses(List<String> additionalAddresses) {
        this.additionalAddresses = additionalAddresses;
    }

    public BuyerStatus getStatus() {
        return status;
    }

    public void setStatus(BuyerStatus status) {
        this.status = status;
    }
}
