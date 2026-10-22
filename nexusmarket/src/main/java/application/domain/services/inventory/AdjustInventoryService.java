package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidInventoryOperationException;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;

/**
 * Manual inventory corrections (ADJUSTMENT movements) by the LOGISTIC_OPERATOR or by
 * the SELLER that owns the product: setting the counted available quantity and
 * marking units as damaged. Stock can never become negative.
 */
public class AdjustInventoryService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;
    private final InventoryAccessService inventoryAccessService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public AdjustInventoryService(InventoryRepositoryPort inventoryRepositoryPort,
                                  InventoryMovementRepositoryPort inventoryMovementRepositoryPort,
                                  InventoryAccessService inventoryAccessService,
                                  RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.inventoryMovementRepositoryPort = inventoryMovementRepositoryPort;
        this.inventoryAccessService = inventoryAccessService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public InventoryItem adjust(User user, InventoryItem item, int newAvailableQuantity, String reason) {
        InventoryItem stored = load(user, item);
        requireReason(reason);
        int before = stored.getAvailableQuantity();
        int delta = stored.adjustAvailableTo(newAvailableQuantity);
        inventoryRepositoryPort.update(stored);
        inventoryMovementRepositoryPort.save(
                InventoryMovement.record(stored, MovementType.ADJUSTMENT, delta, user, null, reason));
        registerOperationAndAuditService.register(OperationType.INVENTORY_ADJUSTMENT, user, stored,
                AuditDetails.of("availableBefore", before, "availableAfter", stored.getAvailableQuantity(),
                        "delta", delta, "reason", reason));
        return stored;
    }

    public InventoryItem markDamaged(User user, InventoryItem item, int quantity, String reason) {
        InventoryItem stored = load(user, item);
        requireReason(reason);
        stored.markDamaged(quantity);
        inventoryRepositoryPort.update(stored);
        inventoryMovementRepositoryPort.save(
                InventoryMovement.record(stored, MovementType.ADJUSTMENT, -quantity, user, null, "Damaged: " + reason));
        registerOperationAndAuditService.register(OperationType.INVENTORY_DAMAGE, user, stored,
                AuditDetails.of("quantity", quantity, "availableAfter", stored.getAvailableQuantity(),
                        "damagedAfter", stored.getDamagedQuantity(), "reason", reason));
        return stored;
    }

    private InventoryItem load(User user, InventoryItem item) {
        inventoryAccessService.requireInventoryRole(user);
        InventoryItem stored = inventoryRepositoryPort.findByIdentifier(item)
                .orElseThrow(() -> new EntityNotFoundException("InventoryItem", item.getIdentifier()));
        inventoryAccessService.requireInventoryAdministrator(user, stored.getProduct());
        return stored;
    }

    private static void requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new InvalidInventoryOperationException("A reason is required.");
        }
    }
}
