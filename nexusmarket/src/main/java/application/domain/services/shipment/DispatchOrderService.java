package application.domain.services.shipment;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidShipmentException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * Dispatches a paid order (LOGISTIC_OPERATOR): creates the shipment IN_TRANSIT and
 * moves the order to SHIPPED. One shipment per order.
 */
public class DispatchOrderService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public DispatchOrderService(OrderRepositoryPort orderRepositoryPort, ShipmentRepositoryPort shipmentRepositoryPort,
                                ValidateUserAuthorizationService validateUserAuthorizationService,
                                RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Shipment dispatch(User user, Order order, String carrier, String trackingNumber) {
        validateUserAuthorizationService.requireRole(user, UserRole.LOGISTIC_OPERATOR);
        Order stored = orderRepositoryPort.findByIdentifier(order)
                .orElseThrow(() -> new EntityNotFoundException("Order", order.getIdentifier()));
        if (shipmentRepositoryPort.findByOrder(stored).isPresent()) {
            throw new InvalidShipmentException("Order " + stored.getIdentifier() + " has already been dispatched.");
        }
        Shipment shipment = Shipment.dispatch(stored, user, carrier, trackingNumber);
        stored.markShipped();
        Shipment saved = shipmentRepositoryPort.save(shipment);
        orderRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.SHIPMENT_DISPATCH, user, saved,
                AuditDetails.of("order", stored.getIdentifier(), "carrier", carrier, "trackingNumber", trackingNumber));
        return saved;
    }
}
