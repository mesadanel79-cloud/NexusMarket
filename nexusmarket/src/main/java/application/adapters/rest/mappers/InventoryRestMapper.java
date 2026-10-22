package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.responses.InventoryItemResponseDTO;
import application.adapters.rest.dtos.responses.InventoryMovementResponseDTO;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;

public final class InventoryRestMapper {

    private InventoryRestMapper() {
    }

    public static InventoryItem reference(String inventoryItemId) {
        InventoryItem item = new InventoryItem();
        item.setIdentifier(inventoryItemId);
        return item;
    }

    public static InventoryItemResponseDTO toResponse(InventoryItem item) {
        return new InventoryItemResponseDTO(item.getIdentifier(),
                item.getWarehouse() == null ? null : item.getWarehouse().getIdentifier(),
                item.getProduct() == null ? null : item.getProduct().getIdentifier(),
                item.getVariant() == null ? null : item.getVariant().getSku(),
                item.getAvailableQuantity(), item.getReservedQuantity(), item.getDamagedQuantity(),
                item.getTotalQuantity());
    }

    public static InventoryMovementResponseDTO toResponse(InventoryMovement movement) {
        return new InventoryMovementResponseDTO(movement.getMovementId(),
                movement.getInventoryItem() == null ? null : movement.getInventoryItem().getIdentifier(),
                code(movement.getMovementType()), movement.getQuantity(), movement.getOccurredAt(),
                movement.getPerformedBy() == null ? null : movement.getPerformedBy().getUserId(),
                movement.getOrder() == null ? null : movement.getOrder().getIdentifier(), movement.getNote());
    }
}
