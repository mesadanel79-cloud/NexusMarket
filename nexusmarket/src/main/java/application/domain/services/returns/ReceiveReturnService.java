package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.InventoryItem;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * A LOGISTIC_OPERATOR receives an approved return; the units go back to the stock of
 * the warehouse they were sold from (RETURN movement). Units returned as DEFECTIVE are
 * stored as damaged stock and can never be reserved again.
 */
public class ReceiveReturnService {

    private final ReturnRequestRepositoryPort returnRequestRepositoryPort;
    private final InventoryAllocationService inventoryAllocationService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ReceiveReturnService(ReturnRequestRepositoryPort returnRequestRepositoryPort,
                                InventoryAllocationService inventoryAllocationService,
                                ValidateUserAuthorizationService validateUserAuthorizationService,
                                RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.returnRequestRepositoryPort = returnRequestRepositoryPort;
        this.inventoryAllocationService = inventoryAllocationService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public ReturnRequest receive(User user, ReturnRequest request) {
        validateUserAuthorizationService.requireRole(user, UserRole.LOGISTIC_OPERATOR);
        ReturnRequest stored = returnRequestRepositoryPort.findByIdentifier(request)
                .orElseThrow(() -> new EntityNotFoundException("ReturnRequest", request.getIdentifier()));
        stored.markReceived(user);
        InventoryItem item = inventoryAllocationService.restock(stored, user);
        returnRequestRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.RETURN_RECEPTION, user, stored,
                AuditDetails.of("inventoryItem", item.getIdentifier(), "quantity", stored.getQuantity(),
                        "availableAfter", item.getAvailableQuantity()));
        return stored;
    }
}
