package application.adapters.rest.dtos.responses;


public record UserResponseDTO(
        Integer userId,
        String username,
        String identification,
        String name,
        String email,
        String role,
        String status,
        String buyerIdentification,
        String sellerIdentification) {
}
