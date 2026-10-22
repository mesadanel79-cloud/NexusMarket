package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record VariantRequestDTO(
        @NotBlank @Pattern(regexp = "[A-Z0-9-]{3,40}") String sku,
        @NotBlank @Size(max = 150) String description,
        @NotNull @Positive BigDecimal price,
        @NotBlank @Pattern(regexp = "COP|USD") String currency) {
}
