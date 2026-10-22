package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.ChangePriceRequestDTO;
import application.adapters.rest.dtos.requests.CreateProductRequestDTO;
import application.adapters.rest.dtos.requests.DamagedInventoryRequestDTO;
import application.adapters.rest.dtos.requests.InventoryAdjustmentRequestDTO;
import application.adapters.rest.dtos.requests.InventoryInflowRequestDTO;
import application.adapters.rest.dtos.requests.VariantRequestDTO;
import application.adapters.rest.dtos.responses.InventoryItemResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.adapters.rest.mappers.CatalogRestMapper;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ParticipantRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.domain.models.User;
import application.domain.ports.in.SellerPort;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller")
public class SellerRestController {

    private final SellerPort sellerPort;

    public SellerRestController(SellerPort sellerPort) {
        this.sellerPort = sellerPort;
    }

    @GetMapping("/store")
    public ResponseEntity<SellerResponseDTO> consultStore(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ParticipantRestMapper.toResponse(sellerPort.consultMyStore(user)));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        sellerPort.changeMyPassword(user, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@AuthenticationPrincipal(expression = "user") User user,
                                                            @Valid @RequestBody CreateProductRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CatalogRestMapper.toResponse(
                sellerPort.createProduct(user, CatalogRestMapper.toDomain(request))));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> consultProducts(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(sellerPort.consultMyProducts(user).stream().map(CatalogRestMapper::toResponse).toList());
    }

    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<ProductResponseDTO> addVariant(@AuthenticationPrincipal(expression = "user") User user,
                                                         @PathVariable String productId,
                                                         @Valid @RequestBody VariantRequestDTO request) {
        return ResponseEntity.ok(CatalogRestMapper.toResponse(sellerPort.addVariant(user,
                CatalogRestMapper.reference(productId), CatalogRestMapper.toDomain(request))));
    }

    @PatchMapping("/products/{productId}/variants/{sku}/price")
    public ResponseEntity<ProductResponseDTO> changePrice(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable String productId, @PathVariable String sku,
                                                          @Valid @RequestBody ChangePriceRequestDTO request) {
        return ResponseEntity.ok(CatalogRestMapper.toResponse(sellerPort.changeVariantPrice(user,
                CatalogRestMapper.reference(productId), sku, CatalogRestMapper.money(request.price(), request.currency()))));
    }

    @PatchMapping("/products/{productId}/suspend")
    public ResponseEntity<ProductResponseDTO> suspend(@AuthenticationPrincipal(expression = "user") User user,
                                                      @PathVariable String productId) {
        return ResponseEntity.ok(CatalogRestMapper.toResponse(
                sellerPort.suspendProduct(user, CatalogRestMapper.reference(productId))));
    }

    @PatchMapping("/products/{productId}/reactivate")
    public ResponseEntity<ProductResponseDTO> reactivate(@AuthenticationPrincipal(expression = "user") User user,
                                                         @PathVariable String productId) {
        return ResponseEntity.ok(CatalogRestMapper.toResponse(
                sellerPort.reactivateProduct(user, CatalogRestMapper.reference(productId))));
    }

    @PatchMapping("/products/{productId}/discontinue")
    public ResponseEntity<ProductResponseDTO> discontinue(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable String productId) {
        return ResponseEntity.ok(CatalogRestMapper.toResponse(
                sellerPort.discontinueProduct(user, CatalogRestMapper.reference(productId))));
    }

    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<List<InventoryItemResponseDTO>> consultInventory(
            @AuthenticationPrincipal(expression = "user") User user, @PathVariable String productId) {
        return ResponseEntity.ok(sellerPort.consultProductInventory(user, CatalogRestMapper.reference(productId))
                .stream().map(InventoryRestMapper::toResponse).toList());
    }

    @PostMapping("/inventory/inflows")
    public ResponseEntity<InventoryItemResponseDTO> registerInflow(@AuthenticationPrincipal(expression = "user") User user,
                                                                   @Valid @RequestBody InventoryInflowRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryRestMapper.toResponse(
                sellerPort.registerInventoryInflow(user, WarehouseRestMapper.reference(request.warehouseId()),
                        CatalogRestMapper.variantReference(request.sku()), request.quantity())));
    }

    @PatchMapping("/inventory/{inventoryItemId}/adjustment")
    public ResponseEntity<InventoryItemResponseDTO> adjust(@AuthenticationPrincipal(expression = "user") User user,
                                                           @PathVariable String inventoryItemId,
                                                           @Valid @RequestBody InventoryAdjustmentRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toResponse(sellerPort.adjustInventory(user,
                InventoryRestMapper.reference(inventoryItemId), request.newAvailableQuantity(), request.reason())));
    }

    @PatchMapping("/inventory/{inventoryItemId}/damaged")
    public ResponseEntity<InventoryItemResponseDTO> markDamaged(@AuthenticationPrincipal(expression = "user") User user,
                                                                @PathVariable String inventoryItemId,
                                                                @Valid @RequestBody DamagedInventoryRequestDTO request) {
        return ResponseEntity.ok(InventoryRestMapper.toResponse(sellerPort.markDamagedInventory(user,
                InventoryRestMapper.reference(inventoryItemId), request.quantity(), request.reason())));
    }

    @GetMapping("/sales")
    public ResponseEntity<List<OrderResponseDTO>> consultSales(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(sellerPort.consultMySales(user).stream().map(OrderRestMapper::toResponse).toList());
    }
}
