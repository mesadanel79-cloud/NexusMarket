package application.domain.models;

import application.domain.valueobjects.Money;
import java.time.LocalDateTime;

/**
 * Money returned to the buyer once a returned item has been received.
 */
public class Refund {

    private String identifier;
    private Money amount;
    private LocalDateTime issuedAt;
    private User issuedBy;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public User getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(User issuedBy) {
        this.issuedBy = issuedBy;
    }
}
