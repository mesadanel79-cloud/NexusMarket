package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;

/**
 * Cancels an order of the buyer.
 *
 * <ul>
 *   <li>PENDING_PAYMENT: no money was received, the reserved stock is released
 *       (movement RESERVATION_RELEASE).</li>
 *   <li>PAID (not shipped yet): the units never left the warehouse, so the SALE is reversed
 *       and they return to available stock (movement RETURN); the whole invoice is refunded
 *       (invoice status REFUNDED) and a REFUND_ISSUANCE operation is recorded.</li>
 *   <li>SHIPPED, DELIVERED or CANCELLED: rejected. After delivery the buyer uses the
 *       return process instead.</li>
 * </ul>
 */
public class CancelOrderService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final InventoryAllocationService inventoryAllocationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public CancelOrderService(OrderRepositoryPort orderRepositoryPort, InvoiceRepositoryPort invoiceRepositoryPort,
                              ValidateOwnershipService validateOwnershipService,
                              InventoryAllocationService inventoryAllocationService,
                              RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
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
        boolean wasPaid = OrderStatus.PAID.equals(stored.getStatus());

        stored.cancel();

        if (wasPaid) {
            inventoryAllocationService.reverseSale(stored, user, "Paid order cancelled before shipping");
            Invoice invoice = invoiceRepositoryPort.findByOrder(stored)
                    .orElseThrow(() -> new EntityNotFoundException("Invoice for order", stored.getIdentifier()));
            Money refunded = invoice.refundRemaining();
            invoiceRepositoryPort.update(invoice);
            registerOperationAndAuditService.register(OperationType.REFUND_ISSUANCE, user, stored,
                    AuditDetails.of("reason", "Paid order cancelled before shipping",
                            "amount", refunded.toString(), "invoice", invoice.getIdentifier(),
                            "invoiceStatus", invoice.getStatus().getCode()));
        } else {
            inventoryAllocationService.release(stored, user, "Cancelled by buyer");
        }
        orderRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.ORDER_CANCELLATION, user, stored,
                AuditDetails.of("previousStatus", previous, "newStatus", stored.getStatus().getCode(),
                        "refunded", wasPaid));
        return stored;
    }
}
