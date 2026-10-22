package application.domain.models;

/**
 * Outcome of a successful login: the authenticated user and the issued token.
 */
public class AuthenticationResult {

    private final User authenticatedUser;
    private final String token;

    public AuthenticationResult(User authenticatedUser, String token) {
        this.authenticatedUser = authenticatedUser;
        this.token = token;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public String getToken() {
        return token;
    }
}
