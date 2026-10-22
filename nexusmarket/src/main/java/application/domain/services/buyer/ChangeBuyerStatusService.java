package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * Suspends or reactivates the commercial status of a buyer (ADMIN only).
 */
public class ChangeBuyerStatusService {

    private final BuyerRepositoryPort buyerRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ChangeBuyerStatusService(BuyerRepositoryPort buyerRepositoryPort,
                                    ValidateUserAuthorizationService validateUserAuthorizationService,
                                    RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Buyer changeStatus(User user, Buyer target, BuyerStatus newStatus) {
        validateUserAuthorizationService.requireRole(user, UserRole.ADMIN);
        Buyer stored = buyerRepositoryPort.findByIdentification(target)
                .orElseThrow(() -> new EntityNotFoundException("Buyer", target.getIdentification()));
        String previous = stored.getStatus().getCode();
        stored.changeStatus(newStatus);
        buyerRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.BUYER_STATUS_CHANGE, user, null,
                AuditDetails.of("buyer", stored.getIdentification(), "previousStatus", previous,
                        "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
