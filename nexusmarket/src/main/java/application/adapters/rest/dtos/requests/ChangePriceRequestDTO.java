package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ChangePriceRequestDTO(
        @NotNull @Positive BigDecimal price,
        @NotBlank @Pattern(regexp = "COP|USD") String currency) {
}
