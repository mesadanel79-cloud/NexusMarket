package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Warehouse;
import application.domain.models.Seller;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {
    }

    public static WarehouseJpaEntity toEntity(Warehouse domain) {
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setName(domain.getName());
        entity.setCity(domain.getCity());
        entity.setAddress(domain.getAddress());
        entity.setWarehouseType(code(domain.getWarehouseType()));
        entity.setOwnerSellerIdentification(domain.getOwner() == null ? null : domain.getOwner().getIdentification());
        entity.setStatus(code(domain.getStatus()));
        return entity;
    }

    /** The owner seller (for SELLER warehouses) is resolved by the adapter. */
    public static Warehouse toDomain(WarehouseJpaEntity entity, Seller owner) {
        if (entity == null) {
            return null;
        }
        Warehouse domain = new Warehouse();
        domain.setIdentifier(entity.getIdentifier());
        domain.setName(entity.getName());
        domain.setCity(entity.getCity());
        domain.setAddress(entity.getAddress());
        domain.setWarehouseType(WarehouseType.fromCode(entity.getWarehouseType()));
        domain.setOwner(owner);
        domain.setStatus(WarehouseStatus.fromCode(entity.getStatus()));
        return domain;
    }

    public static Warehouse reference(String identifier) {
        if (identifier == null) {
            return null;
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(identifier);
        return warehouse;
    }
}
