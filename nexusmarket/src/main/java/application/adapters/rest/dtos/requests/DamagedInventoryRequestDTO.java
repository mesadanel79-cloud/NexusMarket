package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DamagedInventoryRequestDTO(
        @NotNull @Positive Integer quantity,
        @NotBlank @Size(max = 200) String reason) {
}
