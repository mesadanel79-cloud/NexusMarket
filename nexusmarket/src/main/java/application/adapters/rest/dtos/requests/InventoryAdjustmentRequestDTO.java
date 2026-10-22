package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record InventoryAdjustmentRequestDTO(
        @NotNull @PositiveOrZero Integer newAvailableQuantity,
        @NotBlank @Size(max = 200) String reason) {
}
