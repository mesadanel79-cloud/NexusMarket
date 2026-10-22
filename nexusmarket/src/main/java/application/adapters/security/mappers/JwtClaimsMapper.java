package application.adapters.security.mappers;

import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import io.jsonwebtoken.Claims;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bidirectional mapping between the {@link User} Domain Model and the JWT claims.
 *
 * <p>The token carries the data needed to rebuild the User used by the use cases:
 * {@code sub} (user id), {@code ver} (token version), username, role, status,
 * identification, name, email and the buyer / seller profile identifiers.
 */
public final class JwtClaimsMapper {

    public static final String VERSION = "ver";
    public static final String USERNAME = "username";
    public static final String ROLE = "role";
    public static final String STATUS = "status";
    public static final String IDENTIFICATION = "identification";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String BUYER_ID = "buyerId";
    public static final String SELLER_ID = "sellerId";

    private JwtClaimsMapper() {
    }

    public static Map<String, Object> toClaims(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put(VERSION, user.getAuthTokenVersion() == null ? 1 : user.getAuthTokenVersion());
        claims.put(USERNAME, user.getUsername());
        claims.put(ROLE, user.getRole() == null ? null : user.getRole().getCode());
        claims.put(STATUS, user.getStatus() == null ? null : user.getStatus().getCode());
        claims.put(IDENTIFICATION, user.getIdentification());
        claims.put(NAME, user.getName());
        claims.put(EMAIL, user.getEmail());
        if (user.getBuyer() != null) {
            claims.put(BUYER_ID, user.getBuyer().getIdentification());
        }
        if (user.getSeller() != null) {
            claims.put(SELLER_ID, user.getSeller().getIdentification());
        }
        claims.values().removeIf(value -> value == null);
        return claims;
    }

    public static User toDomain(Claims claims) {
        User user = new User();
        user.setUserId(Integer.valueOf(claims.getSubject()));
        user.setAuthTokenVersion(extractVersion(claims));
        user.setUsername(claims.get(USERNAME, String.class));
        user.setRole(UserRole.fromCode(claims.get(ROLE, String.class)));
        user.setStatus(UserStatus.fromCode(claims.get(STATUS, String.class)));
        user.setIdentification(claims.get(IDENTIFICATION, String.class));
        user.setName(claims.get(NAME, String.class));
        user.setEmail(claims.get(EMAIL, String.class));
        String buyerId = claims.get(BUYER_ID, String.class);
        if (buyerId != null) {
            Buyer buyer = new Buyer();
            buyer.setIdentification(buyerId);
            user.setBuyer(buyer);
        }
        String sellerId = claims.get(SELLER_ID, String.class);
        if (sellerId != null) {
            Seller seller = new Seller();
            seller.setIdentification(sellerId);
            user.setSeller(seller);
        }
        return user;
    }

    public static Integer extractVersion(Claims claims) {
        Object version = claims.get(VERSION);
        if (version instanceof Number number) {
            return number.intValue();
        }
        return version == null ? null : Integer.valueOf(version.toString());
    }
}
