package application.domain.services.order;

import application.domain.exceptions.InvalidOrderException;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Converts the buyer's cart into an order: CART -> stock RESERVATION ->
 * PENDING_PAYMENT, and empties the cart.
 */
public class CheckoutService {

    private final CartRepositoryPort cartRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final InventoryAllocationService inventoryAllocationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public CheckoutService(CartRepositoryPort cartRepositoryPort, OrderRepositoryPort orderRepositoryPort,
                           ValidateOwnershipService validateOwnershipService,
                           InventoryAllocationService inventoryAllocationService,
                           RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.cartRepositoryPort = cartRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.inventoryAllocationService = inventoryAllocationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Order checkout(User user, String shippingAddress) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Cart cart = cartRepositoryPort.findByBuyer(buyer)
                .orElseThrow(() -> new InvalidOrderException("The cart is empty."));
        cart.setBuyer(buyer);
        Order order = orderRepositoryPort.save(Order.draftFrom(cart, shippingAddress));
        inventoryAllocationService.reserve(order, user);
        order.submitForPayment();
        orderRepositoryPort.update(order);
        cart.clear();
        cartRepositoryPort.save(cart);
        registerOperationAndAuditService.register(OperationType.ORDER_CHECKOUT, user, order,
                AuditDetails.of("buyer", buyer.getIdentification(), "lines", order.getLines().size(),
                        "total", order.getTotalAmount().toString(), "status", order.getStatus().getCode()));
        return order;
    }
}
