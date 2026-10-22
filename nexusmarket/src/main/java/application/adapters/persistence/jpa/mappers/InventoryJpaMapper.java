package application.adapters.persistence.jpa.mappers;

import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.code;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.userId;
import static application.adapters.persistence.jpa.mappers.JpaMapperSupport.userReference;

import application.adapters.persistence.jpa.entities.InventoryItemJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.valueobjects.MovementType;

public final class InventoryJpaMapper {

    private InventoryJpaMapper() {
    }

    public static InventoryItemJpaEntity toEntity(InventoryItem domain) {
        InventoryItemJpaEntity entity = new InventoryItemJpaEntity();
        entity.setIdentifier(domain.getIdentifier());
        entity.setWarehouseIdentifier(domain.getWarehouse().getIdentifier());
        entity.setProductIdentifier(domain.getProduct().getIdentifier());
        entity.setSku(domain.getVariant().getSku());
        entity.setAvailableQuantity(domain.getAvailableQuantity());
        entity.setReservedQuantity(domain.getReservedQuantity());
        entity.setDamagedQuantity(domain.getDamagedQuantity());
        return entity;
    }

    public static InventoryItem toDomain(InventoryItemJpaEntity entity, Warehouse warehouse, Product product) {
        InventoryItem domain = new InventoryItem();
        domain.setIdentifier(entity.getIdentifier());
        domain.setWarehouse(warehouse);
        domain.setProduct(product);
        domain.setVariant(product == null ? null : product.findVariant(entity.getSku()).orElse(null));
        domain.setAvailableQuantity(entity.getAvailableQuantity());
        domain.setReservedQuantity(entity.getReservedQuantity());
        domain.setDamagedQuantity(entity.getDamagedQuantity());
        return domain;
    }

    public static InventoryMovementJpaEntity toEntity(InventoryMovement domain) {
        InventoryMovementJpaEntity entity = new InventoryMovementJpaEntity();
        entity.setMovementId(domain.getMovementId());
        entity.setInventoryItemIdentifier(domain.getInventoryItem().getIdentifier());
        entity.setMovementType(code(domain.getMovementType()));
        entity.setQuantity(domain.getQuantity());
        entity.setOccurredAt(domain.getOccurredAt());
        entity.setPerformedByUserId(userId(domain.getPerformedBy()));
        entity.setOrderIdentifier(domain.getOrder() == null ? null : domain.getOrder().getIdentifier());
        entity.setNote(domain.getNote());
        return entity;
    }

    public static InventoryMovement toDomain(InventoryMovementJpaEntity entity, InventoryItem item) {
        InventoryMovement domain = new InventoryMovement();
        domain.setMovementId(entity.getMovementId());
        domain.setInventoryItem(item);
        domain.setMovementType(MovementType.fromCode(entity.getMovementType()));
        domain.setQuantity(entity.getQuantity());
        domain.setOccurredAt(entity.getOccurredAt());
        domain.setPerformedBy(userReference(entity.getPerformedByUserId()));
        if (entity.getOrderIdentifier() != null) {
            Order order = new Order();
            order.setIdentifier(entity.getOrderIdentifier());
            domain.setOrder(order);
        }
        domain.setNote(entity.getNote());
        return domain;
    }
}
