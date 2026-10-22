package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

/**
 * Blocks or unblocks a user (ADMIN only). Previously issued tokens become invalid.
 */
public class ChangeUserStatusService {

    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;

    public ChangeUserStatusService(UserRepositoryPort userRepositoryPort,
                                   ValidateUserAuthorizationService validateUserAuthorizationService,
                                   RegisterOperationAndAuditService registerOperationAndAuditService) {
        this.userRepositoryPort = userRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
    }

    public User changeStatus(User admin, User target, UserStatus newStatus) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        User stored = userRepositoryPort.findById(target)
                .orElseThrow(() -> new EntityNotFoundException("User", String.valueOf(target.getUserId())));
        if (stored.getUserId().equals(admin.getUserId())) {
            throw new UnauthorizedOperationException("Administrators cannot change their own status.");
        }
        String previous = stored.getStatus().getCode();
        stored.changeStatus(newStatus);
        userRepositoryPort.update(stored);
        registerOperationAndAuditService.register(OperationType.USER_STATUS_CHANGE, admin, null,
                AuditDetails.of("userId", stored.getUserId(), "previousStatus", previous,
                        "newStatus", stored.getStatus().getCode()));
        return stored;
    }
}
