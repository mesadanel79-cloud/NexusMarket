package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.ChangeStatusRequestDTO;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterStaffUserRequestDTO;
import application.adapters.rest.dtos.requests.ReviewReturnRequestDTO;
import application.adapters.rest.dtos.requests.WarehouseRequestDTO;
import application.adapters.rest.dtos.responses.AdministrativeReportResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.ReturnRequestResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.adapters.rest.mappers.OperationRestMapper;
import application.adapters.rest.mappers.ParticipantRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.enums.ApprovalDecision;
import application.domain.models.User;
import application.domain.ports.in.AdminPort;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
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
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private final AdminPort adminPort;

    public AdminRestController(AdminPort adminPort) {
        this.adminPort = adminPort;
    }

    @PostMapping("/sellers")
    public ResponseEntity<UserResponseDTO> registerSeller(@AuthenticationPrincipal(expression = "user") User admin,
                                                          @Valid @RequestBody RegisterSellerRequestDTO request) {
        User saved = adminPort.registerSeller(admin, UserRestMapper.toNewSellerUser(request),
                UserRestMapper.toFirstWarehouse(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toResponse(saved));
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<SellerResponseDTO>> consultSellers(@AuthenticationPrincipal(expression = "user") User admin) {
        return ResponseEntity.ok(adminPort.consultSellers(admin).stream().map(ParticipantRestMapper::toResponse).toList());
    }

    @PatchMapping("/sellers/{identification}/status")
    public ResponseEntity<SellerResponseDTO> changeSellerStatus(@AuthenticationPrincipal(expression = "user") User admin,
                                                                @PathVariable String identification,
                                                                @Valid @RequestBody ChangeStatusRequestDTO request) {
        return ResponseEntity.ok(ParticipantRestMapper.toResponse(adminPort.changeSellerStatus(admin,
                ParticipantRestMapper.sellerReference(identification), SellerStatus.fromCode(request.status()))));
    }

    @PatchMapping("/buyers/{identification}/status")
    public ResponseEntity<BuyerResponseDTO> changeBuyerStatus(@AuthenticationPrincipal(expression = "user") User admin,
                                                              @PathVariable String identification,
                                                              @Valid @RequestBody ChangeStatusRequestDTO request) {
        return ResponseEntity.ok(ParticipantRestMapper.toResponse(adminPort.changeBuyerStatus(admin,
                ParticipantRestMapper.buyerReference(identification), BuyerStatus.fromCode(request.status()))));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> registerStaffUser(@AuthenticationPrincipal(expression = "user") User admin,
                                                             @Valid @RequestBody RegisterStaffUserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toResponse(
                adminPort.registerStaffUser(admin, UserRestMapper.toNewStaffUser(request))));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> consultUsers(@AuthenticationPrincipal(expression = "user") User admin) {
        return ResponseEntity.ok(adminPort.consultUsers(admin).stream().map(UserRestMapper::toResponse).toList());
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserResponseDTO> changeUserStatus(@AuthenticationPrincipal(expression = "user") User admin,
                                                            @PathVariable Integer userId,
                                                            @Valid @RequestBody ChangeStatusRequestDTO request) {
        return ResponseEntity.ok(UserRestMapper.toResponse(adminPort.changeUserStatus(admin,
                UserRestMapper.reference(userId), UserStatus.fromCode(request.status()))));
    }

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponseDTO> registerWarehouse(@AuthenticationPrincipal(expression = "user") User admin,
                                                                  @Valid @RequestBody WarehouseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(WarehouseRestMapper.toResponse(
                adminPort.registerWarehouse(admin, WarehouseRestMapper.toDomain(request))));
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponseDTO>> consultWarehouses(@AuthenticationPrincipal(expression = "user") User admin) {
        return ResponseEntity.ok(adminPort.consultWarehouses(admin).stream().map(WarehouseRestMapper::toResponse).toList());
    }

    @PatchMapping("/warehouses/{warehouseId}/status")
    public ResponseEntity<WarehouseResponseDTO> changeWarehouseStatus(@AuthenticationPrincipal(expression = "user") User admin,
                                                                      @PathVariable String warehouseId,
                                                                      @Valid @RequestBody ChangeStatusRequestDTO request) {
        return ResponseEntity.ok(WarehouseRestMapper.toResponse(adminPort.changeWarehouseStatus(admin,
                WarehouseRestMapper.reference(warehouseId), WarehouseStatus.fromCode(request.status()))));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnRequestResponseDTO>> consultReturns(@AuthenticationPrincipal(expression = "user") User admin,
                                                                         @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminPort.consultReturns(admin, ReturnStatus.fromCode(status)).stream()
                .map(ReturnRestMapper::toResponse).toList());
    }

    @PatchMapping("/returns/{returnId}/review")
    public ResponseEntity<ReturnRequestResponseDTO> reviewReturn(@AuthenticationPrincipal(expression = "user") User admin,
                                                                 @PathVariable String returnId,
                                                                 @Valid @RequestBody ReviewReturnRequestDTO request) {
        return ResponseEntity.ok(ReturnRestMapper.toResponse(adminPort.reviewReturn(admin,
                ReturnRestMapper.reference(returnId), ApprovalDecision.valueOf(request.decision()),
                request.rejectionReason())));
    }

    @PostMapping("/returns/{returnId}/refund")
    public ResponseEntity<ReturnRequestResponseDTO> issueRefund(@AuthenticationPrincipal(expression = "user") User admin,
                                                                @PathVariable String returnId) {
        return ResponseEntity.ok(ReturnRestMapper.toResponse(
                adminPort.issueRefund(admin, ReturnRestMapper.reference(returnId))));
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<AdministrativeReportResponseDTO> consultReport(@AuthenticationPrincipal(expression = "user") User admin) {
        return ResponseEntity.ok(OperationRestMapper.toResponse(adminPort.consultAdministrativeReport(admin)));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponseDTO>> consultAuditLog(@AuthenticationPrincipal(expression = "user") User admin,
                                                                     @RequestParam(required = false) String operationType) {
        return ResponseEntity.ok(adminPort.consultAuditLog(admin, OperationType.fromCode(operationType)).stream()
                .map(OperationRestMapper::toResponse).toList());
    }
}
