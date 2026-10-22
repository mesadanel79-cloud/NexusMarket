package application.adapters.security.dtos;

import application.domain.models.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Security principal that wraps the {@link User} rebuilt from the JWT. Controllers
 * receive it with {@code @AuthenticationPrincipal(expression = "user")}.
 */
public class AuthenticatedUserPrincipal {

    private final User user;

    public AuthenticatedUserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null || user.getRole() == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()));
    }

    @Override
    public String toString() {
        return user == null ? "anonymous" : user.getUsername();
    }
}
