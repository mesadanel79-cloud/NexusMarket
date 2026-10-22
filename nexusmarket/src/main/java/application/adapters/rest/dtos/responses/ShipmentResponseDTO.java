package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

public record ShipmentResponseDTO(
        String shipmentId,
        String orderId,
        Integer logisticOperatorUserId,
        String carrier,
        String trackingNumber,
        String destinationAddress,
        String status,
        LocalDateTime dispatchedAt,
        LocalDateTime deliveredAt) {
}
