package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReturnRequestResponseDTO(
        String returnId,
        String orderId,
        String buyerIdentification,
        String sku,
        int quantity,
        String reason,
        String comment,
        String status,
        LocalDateTime requestedAt,
        Integer reviewedByUserId,
        LocalDateTime reviewedAt,
        String rejectionReason,
        LocalDateTime receivedAt,
        String refundId,
        BigDecimal refundAmount,
        LocalDateTime refundIssuedAt) {
}
