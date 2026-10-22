package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AdministrativeReportResponseDTO(
        LocalDateTime generatedAt,
        Map<String, Long> usersByRole,
        Map<String, Long> ordersByStatus,
        Map<String, Long> productsByStatus,
        Map<String, Long> returnsByStatus,
        BigDecimal totalInvoiced,
        BigDecimal totalRefunded,
        String currency,
        List<InventoryItemResponseDTO> lowStockItems) {
}
