package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;

public record VariantResponseDTO(
        String sku,
        String description,
        BigDecimal price,
        String currency) {
}
