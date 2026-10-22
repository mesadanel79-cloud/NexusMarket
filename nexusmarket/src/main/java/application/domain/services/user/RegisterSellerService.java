package application.domain.services.user;

import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.UserAlreadyExistsException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import application.domain.services.warehouse.RegisterWarehouseService;

/**
 * Seller onboarding (ADMIN only): registers the seller, its SELLER user and its
 * first warehouse. Sellers cannot self-register.
 */
public class RegisterSellerService {

    private final SellerRepositoryPort sellerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final UserRegistrationRulesService userRegistrationRulesService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final RegisterWarehouseService registerWarehouseService;

    public RegisterSellerService(SellerRepositoryPort sellerRepositoryPort, UserRepositoryPort userRepositoryPort,
                                 UserRegistrationRulesService userRegistrationRulesService,
                                 ValidateUserAuthorizationService validateUserAuthorizationService,
                                 RegisterOperationAndAuditService registerOperationAndAuditService,
                                 RegisterWarehouseService registerWarehouseService) {
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.userRegistrationRulesService = userRegistrationRulesService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.registerWarehouseService = registerWarehouseService;
    }

    public User register(User admin, User newUser, Warehouse firstWarehouse) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        if (newUser == null || newUser.getSeller() == null) {
            throw new InvalidUserException("Seller data is required.");
        }
        if (firstWarehouse == null) {
            throw new InvalidUserException("The first warehouse of the seller is required.");
        }
        Seller seller = newUser.getSeller();
        if (sellerRepositoryPort.existsByIdentification(seller)) {
            throw new UserAlreadyExistsException("Seller " + seller.getIdentification() + " is already registered.");
        }
        if (sellerRepositoryPort.existsByEmail(seller)) {
            throw new UserAlreadyExistsException("Email " + seller.getEmail() + " is already registered.");
        }
        seller.register();
        newUser.setRole(UserRole.SELLER);
        newUser.setBuyer(null);
        userRegistrationRulesService.copyIdentity(newUser, seller);
        userRegistrationRulesService.ensureUnique(newUser);
        userRegistrationRulesService.activateAndEncode(newUser);

        Seller savedSeller = sellerRepositoryPort.save(seller);
        newUser.setSeller(savedSeller);
        User savedUser = userRepositoryPort.save(newUser);

        registerOperationAndAuditService.register(OperationType.SELLER_REGISTRATION, admin, null,
                AuditDetails.of("seller", savedSeller.getIdentification(), "storeName", savedSeller.getStoreName()));
        registerOperationAndAuditService.register(OperationType.USER_REGISTRATION, admin, null,
                AuditDetails.of("username", savedUser.getUsername(), "role", UserRole.SELLER.getCode()));
        firstWarehouse.setWarehouseType(WarehouseType.SELLER);
        firstWarehouse.setOwner(savedSeller);
        registerWarehouseService.register(admin, firstWarehouse);
        return savedUser;
    }
}
