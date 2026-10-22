package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;

/**
 * Changes the password of the authenticated user after verifying the current one.
 */
public class ChangePasswordService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ChangePasswordService(UserRepositoryPort userRepositoryPort, PasswordServicePort passwordServicePort,
                                 ValidateUserAuthorizationService validateUserAuthorizationService,
                                 RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        validateUserAuthorizationService.requireActive(user);
        User stored = userRepositoryPort.findById(user)
                .orElseThrow(() -> new EntityNotFoundException("User", String.valueOf(user.getUserId())));
        if (!passwordServicePort.matches(currentPassword, stored.getPassword())) {
            throw new InvalidCredentialsException();
        }
        if (newPassword == null || newPassword.length() < UserRegistrationRulesService.MIN_PASSWORD_LENGTH) {
            throw new InvalidUserException("Password must have at least "
                    + UserRegistrationRulesService.MIN_PASSWORD_LENGTH + " characters.");
        }
        stored.changePassword(passwordServicePort.encode(newPassword));
        userRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.USER_PASSWORD_CHANGE, stored, null,
                AuditDetails.of("userId", stored.getUserId()));
    }
}
