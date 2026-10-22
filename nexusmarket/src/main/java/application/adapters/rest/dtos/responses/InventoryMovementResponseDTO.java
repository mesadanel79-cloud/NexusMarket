package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

public record InventoryMovementResponseDTO(
        Long movementId,
        String inventoryItemId,
        String movementType,
        int quantity,
        LocalDateTime occurredAt,
        Integer performedByUserId,
        String orderId,
        String note) {
}
