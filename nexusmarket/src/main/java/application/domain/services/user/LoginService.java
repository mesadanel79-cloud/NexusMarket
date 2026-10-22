package application.domain.services.user;

import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Authenticates a user by username and password and issues a JWT.
 */
public class LoginService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final JwtServicePort jwtServicePort;

    public LoginService(UserRepositoryPort userRepositoryPort, PasswordServicePort passwordServicePort,
                        JwtServicePort jwtServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.jwtServicePort = jwtServicePort;
    }

    public AuthenticationResult login(User credentials) {
        if (credentials == null || credentials.getUsername() == null || credentials.getPassword() == null) {
            throw new InvalidCredentialsException();
        }
        User stored = userRepositoryPort.findByUsername(credentials).orElseThrow(InvalidCredentialsException::new);
        if (!passwordServicePort.matches(credentials.getPassword(), stored.getPassword())) {
            throw new InvalidCredentialsException();
        }
        if (!stored.isActive()) {
            throw new UnauthorizedOperationException("User " + stored.getUsername() + " is blocked.");
        }
        return new AuthenticationResult(stored, jwtServicePort.generateToken(stored));
    }
}
