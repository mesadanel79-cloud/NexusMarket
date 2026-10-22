package application.adapters.rest.dtos.responses;


public record WarehouseResponseDTO(
        String code,
        String name,
        String city,
        String address,
        String warehouseType,
        String ownerSellerIdentification,
        String status) {
}
