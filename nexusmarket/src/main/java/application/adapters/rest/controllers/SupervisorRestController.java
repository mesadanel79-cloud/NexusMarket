package application.adapters.rest.controllers;

import application.adapters.rest.dtos.responses.AdministrativeReportResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.adapters.rest.dtos.responses.InventoryItemResponseDTO;
import application.adapters.rest.dtos.responses.OperationResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.ReturnRequestResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.CatalogRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OperationRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.SupervisorPort;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SUPERVISOR endpoints: read-only consultation (GET only).
 */
@RestController
@RequestMapping("/api/v1/supervisor")
public class SupervisorRestController {

    private final SupervisorPort supervisorPort;

    public SupervisorRestController(SupervisorPort supervisorPort) {
        this.supervisorPort = supervisorPort;
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<AdministrativeReportResponseDTO> consultReport(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(OperationRestMapper.toResponse(supervisorPort.consultAdministrativeReport(user)));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(supervisorPort.consultAllOrders(user).stream().map(OrderRestMapper::toResponse).toList());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> consultOrder(@AuthenticationPrincipal(expression = "user") User user,
                                                         @PathVariable String orderId) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                supervisorPort.consultOrder(user, OrderRestMapper.orderReference(orderId))));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> consultProducts(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(supervisorPort.consultAllProducts(user).stream().map(CatalogRestMapper::toResponse).toList());
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponseDTO>> consultWarehouses(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(supervisorPort.consultWarehouses(user).stream().map(WarehouseRestMapper::toResponse).toList());
    }

    @GetMapping("/warehouses/{warehouseId}/inventory")
    public ResponseEntity<List<InventoryItemResponseDTO>> consultInventory(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable String warehouseId) {
        return ResponseEntity.ok(supervisorPort.consultWarehouseInventory(user, WarehouseRestMapper.reference(warehouseId))
                .stream().map(InventoryRestMapper::toResponse).toList());
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnRequestResponseDTO>> consultReturns(@AuthenticationPrincipal(expression = "user") User user,
                                                                         @RequestParam(required = false) String status) {
        return ResponseEntity.ok(supervisorPort.consultReturns(user, ReturnStatus.fromCode(status)).stream()
                .map(ReturnRestMapper::toResponse).toList());
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationResponseDTO>> consultOperations(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(supervisorPort.consultOperations(user).stream().map(OperationRestMapper::toResponse).toList());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponseDTO>> consultAuditLog(@AuthenticationPrincipal(expression = "user") User user,
                                                                     @RequestParam(required = false) String operationType) {
        return ResponseEntity.ok(supervisorPort.consultAuditLog(user, OperationType.fromCode(operationType)).stream()
                .map(OperationRestMapper::toResponse).toList());
    }
}
