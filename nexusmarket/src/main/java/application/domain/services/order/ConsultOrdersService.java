package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Order queries with visibility by role: buyers see their orders, sellers the orders
 * containing their products, logistic operators the orders ready to ship and
 * supervisors / admins every order.
 */
public class ConsultOrdersService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultOrdersService(OrderRepositoryPort orderRepositoryPort, ValidateOwnershipService validateOwnershipService,
                                ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public List<Order> consultBuyerOrders(User user) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        return orderRepositoryPort.findByBuyer(buyer);
    }

    public Order consultBuyerOrder(User user, Order order) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Order stored = load(order);
        validateOwnershipService.requireOrderOwnership(buyer, stored);
        return stored;
    }

    public List<Order> consultSellerSales(User user) {
        Seller seller = validateOwnershipService.requireSeller(user);
        return orderRepositoryPort.findBySeller(seller);
    }

    public List<Order> consultOrdersReadyToShip(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.LOGISTIC_OPERATOR);
        return orderRepositoryPort.findByStatus(OrderStatus.PAID).stream()
                .filter(Order::requiresShipping)
                .toList();
    }

    public List<Order> consultAll(User user) {
        validateUserAuthorizationService.requireRole(user, UserRole.SUPERVISOR, UserRole.ADMIN);
        return orderRepositoryPort.findAll();
    }

    public Order consultAnyOrder(User user, Order order) {
        validateUserAuthorizationService.requireRole(user,
                UserRole.LOGISTIC_OPERATOR, UserRole.SUPERVISOR, UserRole.ADMIN);
        return load(order);
    }

    private Order load(Order order) {
        return orderRepositoryPort.findByIdentifier(order)
                .orElseThrow(() -> new EntityNotFoundException("Order", order.getIdentifier()));
    }
}
