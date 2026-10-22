package application.domain.services.seller;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserRole;

/**
 * Suspends or reactivates a seller (ADMIN only).
 */
public class ChangeSellerStatusService {

    private final SellerRepositoryPort sellerRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ChangeSellerStatusService(SellerRepositoryPort sellerRepositoryPort,
                                     ValidateUserAuthorizationService validateUserAuthorizationService,
                                     RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Seller changeStatus(User admin, Seller target, SellerStatus newStatus) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        Seller stored = sellerRepositoryPort.findByIdentification(target)
                .orElseThrow(() -> new EntityNotFoundException("Seller", target.getIdentification()));
        String previous = stored.getStatus().getCode();
        stored.changeStatus(newStatus);
        sellerRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.SELLER_STATUS_CHANGE, admin, null,
                AuditDetails.of("seller", stored.getIdentification(), "previousStatus", previous,
                        "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
