package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRequestRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * The buyer requests the return of units of a delivered order line.
 */
public class RequestReturnService {

    private final ReturnRequestRepositoryPort returnRequestRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;
    private final ValidateOwnershipService validateOwnershipService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public RequestReturnService(ReturnRequestRepositoryPort returnRequestRepositoryPort,
                                OrderRepositoryPort orderRepositoryPort,
                                BusinessConfigurationPort businessConfigurationPort,
                                ValidateOwnershipService validateOwnershipService,
                                RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.returnRequestRepositoryPort = returnRequestRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
        this.validateOwnershipService = validateOwnershipService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public ReturnRequest request(User user, ReturnRequest request) {
        Buyer buyer = validateOwnershipService.requireBuyer(user);
        Order order = orderRepositoryPort.findByIdentifier(request.getOrder())
                .orElseThrow(() -> new EntityNotFoundException("Order", request.getOrder().getIdentifier()));
        int alreadyReturned = returnRequestRepositoryPort.findByOrder(order).stream()
                .filter(existing -> request.getSku() != null && request.getSku().equals(existing.getSku()))
                .filter(ReturnRequest::countsAsReturned)
                .mapToInt(ReturnRequest::getQuantity)
                .sum();
        ReturnRequest created = ReturnRequest.request(order, buyer, request.getSku(), request.getQuantity(),
                request.getReason(), request.getComment(), alreadyReturned,
                businessConfigurationPort.getReturnWindowDays());
        ReturnRequest saved = returnRequestRepositoryPort.save(created);
        registerOperationAndAuditService.register(OperationType.RETURN_REQUEST, user, saved,
                AuditDetails.of("order", order.getIdentifier(), "sku", saved.getSku(), "quantity", saved.getQuantity(),
                        "reason", saved.getReason().getCode()));
        return saved;
    }
}
