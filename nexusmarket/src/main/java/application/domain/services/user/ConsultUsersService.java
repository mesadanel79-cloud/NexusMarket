package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationService;
import application.domain.valueobjects.UserRole;
import java.util.List;

/**
 * Consultation of system users (ADMIN only).
 */
public class ConsultUsersService {

    private final UserRepositoryPort userRepositoryPort;
    private final ValidateUserAuthorizationService validateUserAuthorizationService;

    public ConsultUsersService(UserRepositoryPort userRepositoryPort,
                               ValidateUserAuthorizationService validateUserAuthorizationService) {
        this.userRepositoryPort = userRepositoryPort;
        this.validateUserAuthorizationService = validateUserAuthorizationService;
    }

    public List<User> consultAll(User admin) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        return userRepositoryPort.findAll();
    }

    public User consultById(User admin, User target) {
        validateUserAuthorizationService.requireRole(admin, UserRole.ADMIN);
        return userRepositoryPort.findById(target)
                .orElseThrow(() -> new EntityNotFoundException("User", String.valueOf(target.getUserId())));
    }
}
