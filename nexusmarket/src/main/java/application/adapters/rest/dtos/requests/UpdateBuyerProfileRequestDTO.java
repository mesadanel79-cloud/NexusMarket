package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateBuyerProfileRequestDTO(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Pattern(regexp = "\\d{7,15}", message = "must contain between 7 and 15 digits") String phoneNumber,
        @NotBlank @Size(max = 200) String address,
        @Size(max = 5) List<@NotBlank @Size(max = 200) String> additionalAddresses) {
}
