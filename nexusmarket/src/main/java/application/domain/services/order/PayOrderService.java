package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.invoice.IssueInvoiceService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.PaymentMethod;

/**
 * Registers the payment of an order: PENDING_PAYMENT -> PAID, reserved stock becomes
 * SALE and the invoice is issued. Digital-only orders are delivered immediately.
 *
 * <p>The payment itself is simulated: there is no external payment gateway.
 */
public class PayOrderService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final InventoryAllocationService inventoryAllocationService;
    private final IssueInvoiceService issueInvoiceService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public PayOrderService(OrderRepositoryPort orderRepositoryPort, ValidateOwnershipService validateOwnershipService,
                           InventoryAllocationService inventoryAllocationService,
                           IssueInvoiceService issueInvoiceService,
                           RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.validateOwnershipService = validateOwnershipService;
        this.inventoryAllocationService = inventoryAllocationService;
        this.issueInvoiceService = issueInvoiceService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Order pay(User user, Order order, PaymentMethod paymentMethod) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Order stored = orderRepositoryPort.findByIdentifier(order)
                .orElseThrow(() -> new EntityNotFoundException("Order", order.getIdentifier()));
        validateOwnershipService.requireOrderOwnership(buyer, stored);
        stored.markPaid(paymentMethod);
        inventoryAllocationService.confirmSale(stored, user);
        Invoice invoice = issueInvoiceService.issue(user, stored);
        registerOperationAndAuditService.register(OperationType.ORDER_PAYMENT, user, stored,
                AuditDetails.of("paymentMethod", paymentMethod.getCode(), "total", stored.getTotalAmount().toString(),
                        "invoice", invoice.getIdentifier()));
        if (!stored.requiresShipping()) {
            stored.markDelivered();
            registerOperationAndAuditService.register(OperationType.ORDER_DELIVERY, user, stored,
                    AuditDetails.of("reason", "Digital-only order delivered on payment"));
        }
        orderRepositoryPort.update(stored);
        return stored;
    }
}
