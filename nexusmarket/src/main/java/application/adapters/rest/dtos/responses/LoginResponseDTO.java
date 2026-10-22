package application.adapters.rest.dtos.responses;


public record LoginResponseDTO(
        String token,
        String tokenType,
        long expiresIn,
        UserResponseDTO user) {
}
