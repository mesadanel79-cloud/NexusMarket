package application.adapters.persistence.mongodb.documents;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document (repository DTO) of the audit trail, stored in the
 * {@code audit_logs} collection. User and asset are denormalized so the trail stays
 * readable even if the referenced aggregates change.
 */
@Document(collection = "audit_logs")
public class AuditLogDocument {

    @Id
    private String auditId;
    private String operationType;
    private LocalDateTime operationDate;
    private Integer performedByUserId;
    private String performedByUsername;
    private String userRole;
    private String affectedAssetIdentifier;
    private String affectedAssetType;
    private Map<String, Object> details;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public Integer getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(Integer performedByUserId) {
        this.performedByUserId = performedByUserId;
    }

    public String getPerformedByUsername() {
        return performedByUsername;
    }

    public void setPerformedByUsername(String performedByUsername) {
        this.performedByUsername = performedByUsername;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getAffectedAssetIdentifier() {
        return affectedAssetIdentifier;
    }

    public void setAffectedAssetIdentifier(String affectedAssetIdentifier) {
        this.affectedAssetIdentifier = affectedAssetIdentifier;
    }

    public String getAffectedAssetType() {
        return affectedAssetType;
    }

    public void setAffectedAssetType(String affectedAssetType) {
        this.affectedAssetType = affectedAssetType;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
