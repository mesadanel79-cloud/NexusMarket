package application.domain.models;

import application.domain.valueobjects.OperationType;
import java.time.LocalDateTime;

/**
 * Significant business operation, stored in the relational operation log.
 */
public class Operation {

    private Long operationId;
    private OperationType operationType;
    private LocalDateTime executionDate;
    private User performedBy;
    private MarketplaceAsset affectedAsset;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public MarketplaceAsset getAffectedAsset() {
        return affectedAsset;
    }

    public void setAffectedAsset(MarketplaceAsset affectedAsset) {
        this.affectedAsset = affectedAsset;
    }
}
