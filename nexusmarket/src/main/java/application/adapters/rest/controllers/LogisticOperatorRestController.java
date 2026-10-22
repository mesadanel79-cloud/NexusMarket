package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.DamagedInventoryRequestDTO;
import application.adapters.rest.dtos.requests.DispatchOrderRequestDTO;
import application.adapters.rest.dtos.requests.InventoryAdjustmentRequestDTO;
import application.adapters.rest.dtos.requests.InventoryInflowRequestDTO;
import application.adapters.rest.dtos.responses.InventoryItemResponseDTO;
import application.adapters.rest.dtos.responses.InventoryMovementResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ReturnRequestResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.CatalogRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.LogisticOperatorPort;
import application.domain.valueobjects.ShipmentStatus;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logistics")
public class LogisticOperatorRestController {

    private final LogisticOperatorPort logisticOperatorPort;

    public LogisticOperatorRestController(LogisticOperatorPort logisticOperatorPort) {
        this.logisticOperatorPort = logisticOperatorPort;
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponseDTO>> consultWarehouses(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(logisticOperatorPort.consultWarehouses(user).stream()
                .map(WarehouseRestMapper::toResponse).toList());
    }

    @GetMapping("/warehouses/{warehouseId}/inventory")
    public ResponseEntity<List<InventoryItemResponseDTO>> consultInventory(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable String warehouseId) {
        return ResponseEntity.ok(logisticOperatorPort.consultWarehouseInventory(user,
                WarehouseRestMapper.reference(warehouseId)).stream().map(InventoryRestMapper::toResponse).toList());
    }

    @PostMapping("/inventory/inflows")
    public ResponseEntity<InventoryItemResponseDTO> registerInflow(@AuthenticationPrincipal(expression = "user") User user,
                                                                   @Valid @RequestBody InventoryInflowRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryRestMapper.toResponse(
                logisticOperatorPort.registerInventoryInflow(user, WarehouseRestMapper.reference(request.warehouseId()),
                        CatalogRestMapper.variantReference(request.sku()), request.quantity())));
    }

    @PatchMapping("/inventory/{inventoryItemId}/adjustment")
    public ResponseEntity<InventoryItemResponseDTO> adjust(@AuthenticationPrincipal(expression = "user") User user,
                                                           @PathVariable String inventoryItemId,
                                                           @Valid @RequestBody InventoryAdjustmentRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toResponse(logisticOperatorPort.adjustInventory(user,
                InventoryRestMapper.reference(inventoryItemId), request.newAvailableQuantity(), request.reason())));
    }

    @PatchMapping("/inventory/{inventoryItemId}/damaged")
    public ResponseEntity<InventoryItemResponseDTO> markDamaged(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable String inventoryItemId,
                                                                @Valid @RequestBody DamagedInventoryRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toResponse(logisticOperatorPort.markDamagedInventory(user,
                InventoryRestMapper.reference(inventoryItemId), request.quantity(), request.reason())));
    }

    @GetMapping("/inventory/{inventoryItemId}/movements")
    public ResponseEntity<List<InventoryMovementResponseDTO>> consultMovements(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable String inventoryItemId) {
        return ResponseEntity.ok(logisticOperatorPort.consultInventoryMovements(user,
                InventoryRestMapper.reference(inventoryItemId)).stream().map(InventoryRestMapper::toResponse).toList());
    }

    @GetMapping("/orders/ready-to-ship")
    public ResponseEntity<List<OrderResponseDTO>> consultReadyToShip(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(logisticOperatorPort.consultOrdersReadyToShip(user).stream()
                .map(OrderRestMapper::toResponse).toList());
    }

    @PostMapping("/orders/{orderId}/shipment")
    public ResponseEntity<ShipmentResponseDTO> dispatch(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable String orderId,
                                                        @Valid @RequestBody DispatchOrderRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderRestMapper.toResponse(logisticOperatorPort
                .dispatchOrder(user, OrderRestMapper.orderReference(orderId), request.carrier(), request.trackingNumber())));
    }

    @PatchMapping("/shipments/{shipmentId}/delivery")
    public ResponseEntity<ShipmentResponseDTO> confirmDelivery(@AuthenticationPrincipal(expression = "user") User user,
                                                               @PathVariable String shipmentId) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                logisticOperatorPort.confirmDelivery(user, OrderRestMapper.shipmentReference(shipmentId))));
    }

    @GetMapping("/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> consultShipments(@AuthenticationPrincipal(expression = "user") User user,
                                                                      @RequestParam(required = false) String status) {
        return ResponseEntity.ok(logisticOperatorPort.consultShipments(user, ShipmentStatus.fromCode(status)).stream()
                .map(OrderRestMapper::toResponse).toList());
    }

    @GetMapping("/returns/approved")
    public ResponseEntity<List<ReturnRequestResponseDTO>> consultApprovedReturns(
            @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(logisticOperatorPort.consultApprovedReturns(user).stream()
                .map(ReturnRestMapper::toResponse).toList());
    }

    @PatchMapping("/returns/{returnId}/reception")
    public ResponseEntity<ReturnRequestResponseDTO> receiveReturn(@AuthenticationPrincipal(expression = "user") User user,
                                                                  @PathVariable String returnId) {
        return ResponseEntity.ok(ReturnRestMapper.toResponse(
                logisticOperatorPort.receiveReturn(user, ReturnRestMapper.reference(returnId))));
    }
}
