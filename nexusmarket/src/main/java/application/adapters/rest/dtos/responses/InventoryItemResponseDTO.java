package application.adapters.rest.dtos.responses;


public record InventoryItemResponseDTO(
        String inventoryItemId,
        String warehouseId,
        String productId,
        String sku,
        int availableQuantity,
        int reservedQuantity,
        int damagedQuantity,
        int totalQuantity) {
}
