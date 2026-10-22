package application.adapters.rest.dtos.responses;

import java.util.List;

public record BuyerResponseDTO(
        String identification,
        String name,
        String email,
        String phoneNumber,
        String address,
        List<String> additionalAddresses,
        String status) {
}
