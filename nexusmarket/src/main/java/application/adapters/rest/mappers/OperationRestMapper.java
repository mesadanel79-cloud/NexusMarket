package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.responses.AdministrativeReportResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.adapters.rest.dtos.responses.OperationResponseDTO;
import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.Operation;

/**
 * Mapping of the traceability records and the administrative report.
 */
public final class OperationRestMapper {

    private OperationRestMapper() {
    }

    public static OperationResponseDTO toResponse(Operation operation) {
        return new OperationResponseDTO(operation.getOperationId(), code(operation.getOperationType()),
                operation.getExecutionDate(),
                operation.getPerformedBy() == null ? null : operation.getPerformedBy().getUserId(),
                operation.getPerformedBy() == null ? null : operation.getPerformedBy().getUsername(),
                operation.getPerformedBy() == null ? null : code(operation.getPerformedBy().getRole()),
                operation.getAffectedAsset() == null ? null : operation.getAffectedAsset().getIdentifier());
    }

    public static AuditLogResponseDTO toResponse(AuditLog auditLog) {
        return new AuditLogResponseDTO(auditLog.getAuditId(), code(auditLog.getOperationType()),
                auditLog.getOperationDate(),
                auditLog.getPerformedBy() == null ? null : auditLog.getPerformedBy().getUserId(),
                code(auditLog.getUserRole()),
                auditLog.getAffectedAsset() == null ? null : auditLog.getAffectedAsset().getIdentifier(),
                auditLog.getDetails());
    }

    public static AdministrativeReportResponseDTO toResponse(AdministrativeReport report) {
        return new AdministrativeReportResponseDTO(report.getGeneratedAt(), report.getUsersByRole(),
                report.getOrdersByStatus(), report.getProductsByStatus(), report.getReturnsByStatus(),
                report.getTotalInvoiced().getAmount(), report.getTotalRefunded().getAmount(),
                report.getTotalInvoiced().getCurrency().getCode(),
                report.getLowStockItems().stream().map(InventoryRestMapper::toResponse).toList());
    }
}
