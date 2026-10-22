package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponseDTO(
        String productId,
        String sellerIdentification,
        String storeName,
        String name,
        String description,
        String category,
        String productType,
        String status,
        LocalDateTime createdAt,
        List<VariantResponseDTO> variants) {
}
