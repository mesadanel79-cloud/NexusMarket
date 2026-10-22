package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA persistence entity (repository DTO): Relational log of business operations.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "operations")
public class OperationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "operation_type", length = 40, nullable = false)
    private String operationType;

    @Column(name = "execution_date", nullable = false)
    private LocalDateTime executionDate;

    @Column(name = "performed_by_user_id")
    private Integer performedByUserId;

    @Column(name = "performed_by_username", length = 50)
    private String performedByUsername;

    @Column(name = "performed_by_role", length = 30)
    private String performedByRole;

    @Column(name = "affected_asset_identifier", length = 80)
    private String affectedAssetIdentifier;

    @Column(name = "affected_asset_type", length = 30)
    private String affectedAssetType;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
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

    public String getPerformedByRole() {
        return performedByRole;
    }

    public void setPerformedByRole(String performedByRole) {
        this.performedByRole = performedByRole;
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
}
