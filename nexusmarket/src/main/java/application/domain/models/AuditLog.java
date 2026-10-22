package application.domain.models;

import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Immutable audit trail entry stored in the NoSQL database. {@code details} holds
 * the variable data of each operation type.
 */
public class AuditLog {

    private String auditId;
    private OperationType operationType;
    private LocalDateTime operationDate;
    private User performedBy;
    private UserRole userRole;
    private MarketplaceAsset affectedAsset;
    private Map<String, Object> details;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public MarketplaceAsset getAffectedAsset() {
        return affectedAsset;
    }

    public void setAffectedAsset(MarketplaceAsset affectedAsset) {
        this.affectedAsset = affectedAsset;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
