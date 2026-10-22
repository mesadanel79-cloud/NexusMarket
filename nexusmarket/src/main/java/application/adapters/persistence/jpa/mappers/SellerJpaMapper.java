package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.domain.models.Seller;
import application.domain.valueobjects.SellerStatus;

public final class SellerJpaMapper {

    private SellerJpaMapper() {
    }

    public static SellerJpaEntity toEntity(Seller domain) {
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setIdentification(domain.getIdentification());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setAddress(domain.getAddress());
        entity.setStoreName(domain.getStoreName());
        entity.setStatus(code(domain.getStatus()));
        entity.setRegistrationDate(domain.getRegistrationDate());
        return entity;
    }

    public static Seller toDomain(SellerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Seller domain = new Seller();
        domain.setIdentification(entity.getIdentification());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setPhoneNumber(entity.getPhoneNumber());
        domain.setAddress(entity.getAddress());
        domain.setStoreName(entity.getStoreName());
        domain.setStatus(SellerStatus.fromCode(entity.getStatus()));
        domain.setRegistrationDate(entity.getRegistrationDate());
        return domain;
    }
}
