package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Invalidates every token issued to the user by bumping its token version.
 */
public class LogoutService {

    private final UserRepositoryPort userRepositoryPort;

    public LogoutService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public void logout(User user) {
        if (user == null || user.getUserId() == null) {
            return;
        }
        User stored = userRepositoryPort.findById(user)
                .orElseThrow(() -> new EntityNotFoundException("User", String.valueOf(user.getUserId())));
        stored.bumpAuthTokenVersion();
        userRepositoryPort.update(stored);
    }
}
