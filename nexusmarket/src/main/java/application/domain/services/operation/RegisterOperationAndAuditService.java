package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Single traceability point: every state-changing service registers the Operation
 * (SQL) and its AuditLog (NoSQL). {@code performedBy} is null for system processes
 * such as the automatic expiration of unpaid orders.
 */
public class RegisterOperationAndAuditService {

    private final RegisterOperationService registerOperationService;
    private final RegisterAuditLogService registerAuditLogService;

    public RegisterOperationAndAuditService(RegisterOperationService registerOperationService,
                                            RegisterAuditLogService registerAuditLogService) {
        this.registerOperationService = registerOperationService;
        this.registerAuditLogService = registerAuditLogService;
    }

    public Operation register(OperationType type, User performedBy, MarketplaceAsset asset,
                              Map<String, Object> details) {
        LocalDateTime now = LocalDateTime.now();
        Operation operation = new Operation();
        operation.setOperationType(type);
        operation.setExecutionDate(now);
        operation.setPerformedBy(performedBy);
        operation.setAffectedAsset(asset);
        Operation saved = registerOperationService.execute(operation);

        AuditLog auditLog = new AuditLog();
        auditLog.setOperationType(type);
        auditLog.setOperationDate(now);
        auditLog.setPerformedBy(performedBy);
        auditLog.setUserRole(performedBy == null ? null : performedBy.getRole());
        auditLog.setAffectedAsset(asset);
        auditLog.setDetails(details == null ? new LinkedHashMap<>() : details);
        registerAuditLogService.execute(auditLog);
        return saved;
    }
}
