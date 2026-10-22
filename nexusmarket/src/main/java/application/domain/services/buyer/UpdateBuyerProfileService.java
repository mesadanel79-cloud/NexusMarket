package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UserAlreadyExistsException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateOwnershipService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Updates the contact data and additional addresses of the authenticated buyer and keeps its user in sync.
 */
public class UpdateBuyerProfileService {

    private final ValidateOwnershipService validateOwnershipService;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public UpdateBuyerProfileService(ValidateOwnershipService validateOwnershipService,
                                     BuyerRepositoryPort buyerRepositoryPort, UserRepositoryPort userRepositoryPort,
                                     RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.validateOwnershipService = validateOwnershipService;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public Buyer update(User user, Buyer desired) {
        Buyer stored = validateOwnershipService.requireBuyer(user);
        desired.setIdentification(stored.getIdentification());
        boolean emailChanged = desired.getEmail() != null && !desired.getEmail().equalsIgnoreCase(stored.getEmail());
        if (emailChanged && (buyerRepositoryPort.existsByEmail(desired) || userRepositoryPort.existsByEmail(toUserProbe(desired)))) {
            throw new UserAlreadyExistsException("Email " + desired.getEmail() + " is already registered.");
        }
        stored.updateProfile(desired);
        buyerRepositoryPort.update(stored);

        User storedUser = userRepositoryPort.findById(user)
                .orElseThrow(() -> new EntityNotFoundException("User", String.valueOf(user.getUserId())));
        storedUser.syncContactData(stored);
        userRepositoryPort.update(storedUser);

        registerOperationAndAuditService.register(OperationType.BUYER_UPDATE, user, null,
                AuditDetails.of("buyer", stored.getIdentification(), "emailChanged", emailChanged));
        return stored;
    }

    private static User toUserProbe(Buyer buyer) {
        User probe = new User();
        probe.setEmail(buyer.getEmail());
        return probe;
    }
}
