package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;

public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static UserJpaEntity toEntity(User domain) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setUserId(domain.getUserId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setIdentification(domain.getIdentification());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setAddress(domain.getAddress());
        entity.setRole(code(domain.getRole()));
        entity.setStatus(code(domain.getStatus()));
        entity.setAuthTokenVersion(domain.getAuthTokenVersion() == null ? 1 : domain.getAuthTokenVersion());
        entity.setBuyerIdentification(domain.getBuyer() == null ? null : domain.getBuyer().getIdentification());
        entity.setSellerIdentification(domain.getSeller() == null ? null : domain.getSeller().getIdentification());
        return entity;
    }

    /** Buyer and seller profiles are supplied by the adapter (already resolved). */
    public static User toDomain(UserJpaEntity entity, Buyer buyer, Seller seller) {
        User domain = new User();
        domain.setUserId(entity.getUserId());
        domain.setUsername(entity.getUsername());
        domain.setPassword(entity.getPassword());
        domain.setIdentification(entity.getIdentification());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setPhoneNumber(entity.getPhoneNumber());
        domain.setAddress(entity.getAddress());
        domain.setRole(UserRole.fromCode(entity.getRole()));
        domain.setStatus(UserStatus.fromCode(entity.getStatus()));
        domain.setAuthTokenVersion(entity.getAuthTokenVersion());
        domain.setBuyer(buyer);
        domain.setSeller(seller);
        return domain;
    }
}
