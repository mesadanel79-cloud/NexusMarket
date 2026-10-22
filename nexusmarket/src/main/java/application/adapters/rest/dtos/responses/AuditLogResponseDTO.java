package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;
import java.util.Map;

public record AuditLogResponseDTO(
        String auditId,
        String operationType,
        LocalDateTime operationDate,
        Integer performedByUserId,
        String userRole,
        String affectedAssetId,
        Map<String, Object> details) {
}
