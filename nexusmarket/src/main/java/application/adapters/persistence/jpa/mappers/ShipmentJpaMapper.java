package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.userId;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.userReference;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.valueobjects.ShipmentStatus;

public final class ShipmentJpaMapper {

    private ShipmentJpaMapper() {
    }

    public static ShipmentJpaEntity toEntity(Shipment domain) {
        ShipmentJpaEntity entity = new ShipmentJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setOrderIdentifier(domain.getOrder().getIdentifier());
        entity.setLogisticOperatorUserId(userId(domain.getLogisticOperator()));
        entity.setCarrier(domain.getCarrier());
        entity.setTrackingNumber(domain.getTrackingNumber());
        entity.setDestinationAddress(domain.getDestinationAddress());
        entity.setStatus(code(domain.getStatus()));
        entity.setDispatchedAt(domain.getDispatchedAt());
        entity.setDeliveredAt(domain.getDeliveredAt());
        return entity;
    }

    public static Shipment toDomain(ShipmentJpaEntity entity, Order order) {
        Shipment domain = new Shipment();
        domain.setIdentifier(entity.getIdentifier());
        domain.setOrder(order);
        domain.setLogisticOperator(userReference(entity.getLogisticOperatorUserId()));
        domain.setCarrier(entity.getCarrier());
        domain.setTrackingNumber(entity.getTrackingNumber());
        domain.setDestinationAddress(entity.getDestinationAddress());
        domain.setStatus(ShipmentStatus.fromCode(entity.getStatus()));
        domain.setDispatchedAt(entity.getDispatchedAt());
        domain.setDeliveredAt(entity.getDeliveredAt());
        return domain;
    }
}
