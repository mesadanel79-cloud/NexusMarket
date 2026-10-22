package application.domain.services.warehouse;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseStatus;

/**
 * Activates or deactivates a warehouse (ADMIN only).
 */
public class ChangeWarehouseStatusService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ChangeWarehouseStatusService(WarehouseRepositoryPort warehouseRepositoryPort,
                                        ValidateUserAuthorizationService validateUserAuthorizationService,
                                        RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Warehouse changeStatus(User admin, Warehouse target, WarehouseStatus newStatus) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        Warehouse stored = warehouseRepositoryPort.findByIdentifier(target)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", target.getIdentifier()));
        String previous = stored.getStatus().getCode();
        if (WarehouseStatus.ACTIVE.equals(newStatus)) {
            stored.activate();
        } else if (WarehouseStatus.INACTIVE.equals(newStatus)) {
            stored.deactivate();
        } else {
            throw new InvalidWarehouseException("Target warehouse status is required.");
        }
        warehouseRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.WAREHOUSE_STATUS_CHANGE, admin, stored,
                AuditDetails.of("previousStatus", previous, "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
