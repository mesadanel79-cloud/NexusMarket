package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateProductRequestDTO(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotBlank @Pattern(regexp = "ELECTRONICS|FASHION|HOME|BOOKS|SPORTS|SOFTWARE|OTHER") String category,
        @NotBlank @Pattern(regexp = "PHYSICAL|DIGITAL") String productType,
        @NotEmpty List<@Valid VariantRequestDTO> variants) {
}
