package application.domain.services.shipment;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Shipment queries: the buyer's order tracking and the logistic list by status.
 */
public class ConsultShipmentsService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultShipmentsService(ShipmentRepositoryPort shipmentRepositoryPort, OrderRepositoryPort orderRepositoryPort,
                                   ValidateOwnershipService validateOwnershipService,
                                   ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public Shipment consultOrderShipment(User user, Order order) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Order stored = orderRepositoryPort.findByIdentifier(order)
                .orElseThrow(() -> new EntityNotFoundException("Order", order.getIdentifier()));
        validateOwnershipService.requireOrderOwnership(buyer, stored);
        return shipmentRepositoryPort.findByOrder(stored)
                .orElseThrow(() -> new EntityNotFoundException("Shipment for order", stored.getIdentifier()));
    }

    public List<Shipment> consultByStatus(User user, ShipmentStatus status) {
        validateUserAuthorizationService.requireRole(user,
                UserRole.LOGISTIC_OPERATOR, UserRole.SUPERVISOR, UserRole.ADMIN);
        return shipmentRepositoryPort.findByStatus(status == null ? ShipmentStatus.IN_TRANSIT : status);
    }
}
