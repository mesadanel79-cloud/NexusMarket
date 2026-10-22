package application.domain.services.shipment;

import application.domain.exceptions.EntityNotFoundException;
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
 * Confirms the delivery of a shipment (LOGISTIC_OPERATOR): shipment DELIVERED and
 * order DELIVERED (finalized).
 */
public class ConfirmDeliveryService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ConfirmDeliveryService(ShipmentRepositoryPort shipmentRepositoryPort, OrderRepositoryPort orderRepositoryPort,
                                  ValidateUserAuthorizationService validateUserAuthorizationService,
                                  RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Shipment confirm(User user, Shipment shipment) {
        validateUserAuthorizationService.requireRole(user, UserRole.LOGISTIC_OPERATOR);
        Shipment stored = shipmentRepositoryPort.findByIdentifier(shipment)
                .orElseThrow(() -> new EntityNotFoundException("Shipment", shipment.getIdentifier()));
        Order order = orderRepositoryPort.findByIdentifier(stored.getOrder())
                .orElseThrow(() -> new EntityNotFoundException("Order", stored.getOrder().getIdentifier()));
        stored.confirmDelivery();
        order.markDelivered();
        stored.setOrder(order);
        shipmentRepositoryPort.update(stored);
        orderRepositoryPort.update(order);
        registerOperationAndAuditService.register(OperationType.SHIPMENT_DELIVERY, user, stored,
                AuditDetails.of("order", order.getIdentifier()));
        registerOperationAndAuditService.register(OperationType.ORDER_DELIVERY, user, order,
                AuditDetails.of("shipment", stored.getIdentifier()));
        return stored;
    }
}
