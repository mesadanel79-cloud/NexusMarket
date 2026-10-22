package application.domain.ports.out;

import application.domain.models.User;

/**
 * Issues and reads identity tokens. The token carries the claims needed to rebuild
 * the {@link User} Domain Model ({@code sub}, {@code ver}, role, status, profile ids).
 */
public interface JwtServicePort {

    String generateToken(User user);

    boolean isValid(String token);

    User reconstructUser(String token);
}
