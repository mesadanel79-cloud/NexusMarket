package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Cancels an unpaid order of the buyer and releases its reserved stock.
 */
public class CancelOrderService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final InventoryAllocationService inventoryAllocationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public CancelOrderService(OrderRepositoryPort orderRepositoryPort, ValidateOwnershipService validateOwnershipService,
                              InventoryAllocationService inventoryAllocationService,
                              RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.inventoryAllocationService = inventoryAllocationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Order cancel(User user, Order order) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Order stored = orderRepositoryPort.findByIdentifier(order)
                .orElseThrow(() -> new EntityNotFoundException("Order", order.getIdentifier()));
        validateOwnershipService.requireOrderOwnership(buyer, stored);
        String previous = stored.getStatus().getCode();
        stored.cancel();
        inventoryAllocationService.release(stored, user, "Cancelled by buyer");
        orderRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.ORDER_CANCELLATION, user, stored,
                AuditDetails.of("previousStatus", previous, "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
