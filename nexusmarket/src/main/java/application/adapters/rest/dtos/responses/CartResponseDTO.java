package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponseDTO(
        String cartId,
        List<CartItemResponseDTO> items,
        BigDecimal total,
        String currency,
        LocalDateTime updatedAt) {
}
