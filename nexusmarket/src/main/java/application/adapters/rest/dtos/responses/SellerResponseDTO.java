package application.adapters.rest.dtos.responses;

import java.time.LocalDate;

public record SellerResponseDTO(
        String identification,
        String name,
        String email,
        String phoneNumber,
        String address,
        String storeName,
        String status,
        LocalDate registrationDate) {
}
