package application.adapters.persistence.mongodb;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.adapters.persistence.mongodb.mappers.AuditLogMongoMapper;
import application.adapters.persistence.mongodb.repositories.AuditLogMongoRepository;
import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.valueobjects.OperationType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Audit trail adapter backed by MongoDB ({@code audit_db.audit_logs}). Append-only.
 */
@Repository
public class AuditLogMongoAdapter implements AuditLogRepositoryPort {

    private final AuditLogMongoRepository auditLogRepository;

    public AuditLogMongoAdapter(AuditLogMongoRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogDocument saved = auditLogRepository.save(AuditLogMongoMapper.toDocument(auditLog));
        auditLog.setAuditId(saved.getAuditId());
        return auditLog;
    }

    @Override
    public List<AuditLog> findByUser(User user) {
        return auditLogRepository.findByPerformedByUserIdOrderByOperationDateDesc(user.getUserId()).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByAsset(MarketplaceAsset asset) {
        return auditLogRepository.findByAffectedAssetIdentifierOrderByOperationDateDesc(asset.getIdentifier()).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByOperationType(OperationType operationType) {
        return auditLogRepository.findByOperationTypeOrderByOperationDateDesc(operationType.getCode()).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAllByOrderByOperationDateDesc().stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }
}
