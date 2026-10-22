package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FirstWarehouseRequestDTO(
        @NotBlank @Pattern(regexp = "[A-Z0-9-]{3,20}") String code,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 60) String city,
        @NotBlank @Size(max = 200) String address) {
}
