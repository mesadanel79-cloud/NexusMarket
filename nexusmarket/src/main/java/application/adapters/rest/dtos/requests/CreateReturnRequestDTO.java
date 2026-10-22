package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateReturnRequestDTO(
        @NotBlank String orderId,
        @NotBlank String sku,
        @NotNull @Positive Integer quantity,
        @NotBlank @Pattern(regexp = "DEFECTIVE|WRONG_ITEM|NOT_AS_DESCRIBED|NO_LONGER_NEEDED") String reason,
        @Size(max = 500) String comment) {
}
