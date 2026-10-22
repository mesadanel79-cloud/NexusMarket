package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        String orderId,
        String buyerIdentification,
        String status,
        BigDecimal totalAmount,
        String currency,
        String shippingAddress,
        String paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,
        List<OrderLineResponseDTO> lines) {
}
