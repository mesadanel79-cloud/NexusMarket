package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Size;

public record CheckoutRequestDTO(
        @Size(max = 200) String shippingAddress) {
}
