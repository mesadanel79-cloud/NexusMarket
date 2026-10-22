package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Read access to the operation log and the audit trail (SUPERVISOR and ADMIN only).
 */
public class ConsultOperationsService {

    private final OperationRepositoryPort operationRepositoryPort;
    private final AuditLogRepositoryPort auditLogRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultOperationsService(OperationRepositoryPort operationRepositoryPort,
                                    AuditLogRepositoryPort auditLogRepositoryPort,
                                    ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.operationRepositoryPort = operationRepositoryPort;
        this.auditLogRepositoryPort = auditLogRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public List<Operation> consultOperations(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.SUPERVISOR, UserRole.ADMIN);
        return operationRepositoryPort.findAll();
    }

    public List<AuditLog> consultAuditLog(User user, OperationType operationType) {
        validateUserAuthorizationService.requireRole(user, UserRole.SUPERVISOR, UserRole.ADMIN);
        return operationType == null
                ? auditLogRepositoryPort.findAll()
                : auditLogRepositoryPort.findByOperationType(operationType);
    }
}
