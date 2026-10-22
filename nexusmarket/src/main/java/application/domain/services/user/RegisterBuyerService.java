package application.domain.services.user;

import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.UserAlreadyExistsException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * Public self-registration of a buyer: creates the Buyer profile and its BUYER user.
 */
public class RegisterBuyerService {

    private final BuyerRepositoryPort buyerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final UserRegistrationRulesService userRegistrationRulesService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public RegisterBuyerService(BuyerRepositoryPort buyerRepositoryPort, UserRepositoryPort userRepositoryPort,
                                UserRegistrationRulesService userRegistrationRulesService,
                                RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.userRegistrationRulesService = userRegistrationRulesService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public User register(User newUser) {
        if (newUser == null || newUser.getBuyer() == null) {
            throw new InvalidUserException("Buyer data is required.");
        }
        Buyer buyer = newUser.getBuyer();
        if (buyerRepositoryPort.existsByIdentification(buyer)) {
            throw new UserAlreadyExistsException("Buyer " + buyer.getIdentification() + " is already registered.");
        }
        if (buyerRepositoryPort.existsByEmail(buyer)) {
            throw new UserAlreadyExistsException("Email " + buyer.getEmail() + " is already registered.");
        }
        buyer.register();
        newUser.setRole(UserRole.BUYER);
        newUser.setSeller(null);
        userRegistrationRulesService.copyIdentity(newUser, buyer);
        userRegistrationRulesService.ensureUnique(newUser);
        userRegistrationRulesService.activateAndEncode(newUser);

        Buyer savedBuyer = buyerRepositoryPort.save(buyer);
        newUser.setBuyer(savedBuyer);
        User savedUser = userRepositoryPort.save(newUser);

        registerOperationAndAuditService.register(OperationType.BUYER_REGISTRATION, savedUser, null,
                AuditDetails.of("buyer", savedBuyer.getIdentification(), "status", savedBuyer.getStatus().getCode()));
        registerOperationAndAuditService.register(OperationType.USER_REGISTRATION, savedUser, null,
                AuditDetails.of("username", savedUser.getUsername(), "role", UserRole.BUYER.getCode()));
        return savedUser;
    }
}
