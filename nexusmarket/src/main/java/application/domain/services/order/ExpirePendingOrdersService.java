package application.domain.services.order;

import application.domain.models.Order;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.inventory.InventoryAllocationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * System process: cancels orders that stayed in PENDING_PAYMENT longer than the
 * configured window and releases their reserved stock. Runs without an interactive user.
 */
public class ExpirePendingOrdersService {

    static final String EXPIRATION_REASON = "Expired: payment not received within the allowed time";

    private final OrderRepositoryPort orderRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;
    private final InventoryAllocationService inventoryAllocationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ExpirePendingOrdersService(OrderRepositoryPort orderRepositoryPort,
                                      BusinessConfigurationPort businessConfigurationPort,
                                      InventoryAllocationService inventoryAllocationService,
                                      RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
        this.inventoryAllocationService = inventoryAllocationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public List<Order> expireOverdueOrders() {
        LocalDateTime now = LocalDateTime.now();
        int minutes = businessConfigurationPort.getPaymentExpirationMinutes();
        List<Order> expired = new ArrayList<>();
        for (Order order : orderRepositoryPort.findByStatus(OrderStatus.PENDING_PAYMENT)) {
            if (!order.isPaymentExpired(now, minutes)) {
                continue;
            }
            order.cancel();
            inventoryAllocationService.release(order, null, EXPIRATION_REASON);
            orderRepositoryPort.update(order);
            registerOperationAndAuditService.register(OperationType.ORDER_EXPIRATION, null, order,
                    AuditDetails.of("reason", EXPIRATION_REASON, "expiredAt", now.toString(),
                            "buyer", order.getBuyer() == null ? null : order.getBuyer().getIdentification()));
            expired.add(order);
        }
        return expired;
    }
}
