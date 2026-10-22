package application.domain.services.user;

import application.domain.exceptions.InvalidUserException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;

/**
 * Registration of staff users (LOGISTIC_OPERATOR, SUPERVISOR, ADMIN) by an ADMIN.
 */
public class RegisterStaffUserService {

    private final UserRepositoryPort userRepositoryPort;
    private final UserRegistrationRulesService userRegistrationRulesService;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public RegisterStaffUserService(UserRepositoryPort userRepositoryPort,
                                    UserRegistrationRulesService userRegistrationRulesService,
                                    ValidateUserAuthorizationService validateUserAuthorizationService,
                                    RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.userRepositoryPort = userRepositoryPort;
        this.userRegistrationRulesService = userRegistrationRulesService;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public User register(User admin, User newUser) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        if (newUser == null) {
            throw new InvalidUserException("User data is required.");
        }
        if (!newUser.isStaff()) {
            throw new InvalidUserException("Only LOGISTIC_OPERATOR, SUPERVISOR or ADMIN users can be registered here.");
        }
        newUser.setBuyer(null);
        newUser.setSeller(null);
        userRegistrationRulesService.ensureUnique(newUser);
        userRegistrationRulesService.activateAndEncode(newUser);
        User saved = userRepositoryPort.save(newUser);
        registerOperationAndAuditService.register(OperationType.USER_REGISTRATION, admin, null,
                AuditDetails.of("username", saved.getUsername(), "role", saved.getRole().getCode()));
        return saved;
    }
}
