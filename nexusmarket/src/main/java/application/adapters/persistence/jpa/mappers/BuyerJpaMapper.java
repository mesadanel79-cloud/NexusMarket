package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import java.util.ArrayList;

public final class BuyerJpaMapper {

    private BuyerJpaMapper() {
    }

    public static BuyerJpaEntity toEntity(Buyer domain) {
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setIdentification(domain.getIdentification());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setAddress(domain.getAddress());
        entity.setAdditionalAddresses(new ArrayList<>(domain.getAdditionalAddresses()));
        entity.setStatus(code(domain.getStatus()));
        return entity;
    }

    public static Buyer toDomain(BuyerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Buyer domain = new Buyer();
        domain.setIdentification(entity.getIdentification());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setPhoneNumber(entity.getPhoneNumber());
        domain.setAddress(entity.getAddress());
        domain.setAdditionalAddresses(new ArrayList<>(entity.getAdditionalAddresses()));
        domain.setStatus(BuyerStatus.fromCode(entity.getStatus()));
        return domain;
    }
}
