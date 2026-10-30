package application.adapters.security;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.services.user.LoadAuthenticatedUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates the Bearer token, rebuilds the {@link User} from its claims and checks
 * against the stored user that it is still ACTIVE and that the token version
 * ({@code ver}) is current, so logout, password and status changes revoke old tokens.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtServicePort jwtServicePort;
    private final LoadAuthenticatedUserService loadAuthenticatedUserService;

    public JwtAuthenticationFilter(JwtServicePort jwtServicePort,
                                   LoadAuthenticatedUserService loadAuthenticatedUserService) {
        this.jwtServicePort = jwtServicePort;
        this.loadAuthenticatedUserService = loadAuthenticatedUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtServicePort.isValid(token)) {
                    User tokenUser = jwtServicePort.reconstructUser(token);
                    User stored = loadAuthenticatedUserService.loadById(tokenUser.getUserId());
                    boolean current = stored.isActive()
                            && stored.getAuthTokenVersion() != null
                            && stored.getAuthTokenVersion().equals(tokenUser.getAuthTokenVersion());
                    if (current) {
                        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(tokenUser);
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
                    } else {
                        log.warn("Rejected token on {} {}: user {} is blocked or the token was revoked (logout, "
                                + "password or status change).", request.getMethod(), request.getRequestURI(),
                                stored.getUsername());
                    }
                } else {
                    log.warn("Rejected token on {} {}: invalid signature, malformed or expired.",
                            request.getMethod(), request.getRequestURI());
                }
            } catch (RuntimeException ex) {
                log.warn("Rejected token on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
