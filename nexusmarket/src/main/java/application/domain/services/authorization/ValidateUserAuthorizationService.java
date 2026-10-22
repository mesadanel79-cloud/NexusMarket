package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Validates that the acting user is present, ACTIVE and holds one of the roles
 * allowed for the operation.
 */
public class ValidateUserAuthorizationService {

    public void requireActive(User user) {
        if (user == null) {
            throw new UnauthorizedOperationException("An authenticated user is required.");
        }
        if (!user.isActive()) {
            throw new UnauthorizedOperationException("User " + user.getUsername() + " is not active.");
        }
    }

    public void requireRole(User user, UserRole... allowedRoles) {
        requireActive(user);
        if (!user.hasAnyRole(allowedRoles)) {
            String allowed = Arrays.stream(allowedRoles).map(UserRole::getCode).collect(Collectors.joining(", "));
            throw new UnauthorizedOperationException("Operation allowed only for roles: " + allowed + ".");
        }
    }
}
