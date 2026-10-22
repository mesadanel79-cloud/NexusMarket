package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryInflowRequestDTO(
        @NotBlank String warehouseId,
        @NotBlank String sku,
        @NotNull @Positive Integer quantity) {
}
