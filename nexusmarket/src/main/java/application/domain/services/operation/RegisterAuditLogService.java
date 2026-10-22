package application.domain.services.operation;

import application.domain.exceptions.InvalidAuditLogException;
import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;

/**
 * Appends an immutable entry to the NoSQL audit trail.
 */
public class RegisterAuditLogService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public RegisterAuditLogService(AuditLogRepositoryPort auditLogRepositoryPort) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public AuditLog execute(AuditLog auditLog) {
        if (auditLog == null || auditLog.getOperationType() == null) {
            throw new InvalidAuditLogException("Audit log operation type is required.");
        }
        if (auditLog.getOperationDate() == null) {
            throw new InvalidAuditLogException("Audit log date is required.");
        }
        if (auditLog.getDetails() == null) {
            throw new InvalidAuditLogException("Audit log details are required.");
        }
        return auditLogRepositoryPort.save(auditLog);
    }
}
