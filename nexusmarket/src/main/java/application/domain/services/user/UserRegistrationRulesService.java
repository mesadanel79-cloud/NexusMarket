package application.domain.services.user;

import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.UserAlreadyExistsException;
import application.domain.models.Person;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Shared registration rules for every kind of user: unique username, identification
 * and email; minimum password length; password stored encoded.
 */
public class UserRegistrationRulesService {

    static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public UserRegistrationRulesService(UserRepositoryPort userRepositoryPort, PasswordServicePort passwordServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
    }

    /** Copies identity and contact data from the commercial profile into the user. */
    public void copyIdentity(User user, Person profile) {
        user.setIdentification(profile.getIdentification());
        user.setName(profile.getName());
        user.setEmail(profile.getEmail());
        user.setPhoneNumber(profile.getPhoneNumber());
        user.setAddress(profile.getAddress());
    }

    public void ensureUnique(User user) {
        if (userRepositoryPort.existsByUsername(user)) {
            throw new UserAlreadyExistsException("Username " + user.getUsername() + " is already in use.");
        }
        if (userRepositoryPort.existsByIdentification(user)) {
            throw new UserAlreadyExistsException("Identification " + user.getIdentification() + " is already registered.");
        }
        if (userRepositoryPort.existsByEmail(user)) {
            throw new UserAlreadyExistsException("Email " + user.getEmail() + " is already registered.");
        }
    }

    /** Validates and activates the user, then replaces the raw password with its hash. */
    public void activateAndEncode(User user) {
        String rawPassword = user.getPassword();
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidUserException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        user.activateOnRegistration();
        user.setPassword(passwordServicePort.encode(rawPassword));
    }
}
