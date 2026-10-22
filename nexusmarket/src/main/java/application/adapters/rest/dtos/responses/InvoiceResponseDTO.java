package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponseDTO(
        String invoiceId,
        String orderId,
        String buyerIdentification,
        LocalDateTime issueDate,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal total,
        BigDecimal refundedAmount,
        String currency,
        String status) {
}
