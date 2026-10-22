package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Loads the persisted user referenced by a token subject, used by the security
 * adapter to verify token version and status.
 */
public class LoadAuthenticatedUserService {

    private final UserRepositoryPort userRepositoryPort;

    public LoadAuthenticatedUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User loadById(Integer userId) {
        User probe = new User();
        probe.setUserId(userId);
        return userRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("User", String.valueOf(userId)));
    }
}
