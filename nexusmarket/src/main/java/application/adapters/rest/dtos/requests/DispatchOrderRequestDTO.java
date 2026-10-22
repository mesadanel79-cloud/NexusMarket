package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DispatchOrderRequestDTO(
        @NotBlank @Size(max = 60) String carrier,
        @NotBlank @Size(max = 60) String trackingNumber) {
}
