package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;

public record OrderLineResponseDTO(
        int lineNumber,
        String productId,
        String productName,
        String productType,
        String sku,
        String sellerIdentification,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String warehouseId) {
}
