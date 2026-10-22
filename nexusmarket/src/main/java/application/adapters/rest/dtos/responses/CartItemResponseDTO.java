package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;

public record CartItemResponseDTO(
        String productId,
        String productName,
        String sku,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String currency) {
}
