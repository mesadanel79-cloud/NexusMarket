package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

public record OperationResponseDTO(
        Long operationId,
        String operationType,
        LocalDateTime executionDate,
        Integer performedByUserId,
        String performedByUsername,
        String performedByRole,
        String affectedAssetId) {
}
