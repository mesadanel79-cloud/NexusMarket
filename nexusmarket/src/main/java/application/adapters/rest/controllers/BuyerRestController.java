package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.CartItemRequestDTO;
import application.adapters.rest.dtos.requests.ChangePasswordRequestDTO;
import application.adapters.rest.dtos.requests.CheckoutRequestDTO;
import application.adapters.rest.dtos.requests.CreateReturnRequestDTO;
import application.adapters.rest.dtos.requests.PayOrderRequestDTO;
import application.adapters.rest.dtos.requests.UpdateBuyerProfileRequestDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.CartResponseDTO;
import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ReturnRequestResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.ParticipantRestMapper;
import application.adapters.rest.mappers.ReturnRestMapper;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.valueobjects.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buyer")
public class BuyerRestController {

    private final BuyerPort buyerPort;

    public BuyerRestController(BuyerPort buyerPort) {
        this.buyerPort = buyerPort;
    }

    @GetMapping("/profile")
    public ResponseEntity<BuyerResponseDTO> consultProfile(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ParticipantRestMapper.toResponse(buyerPort.consultMyProfile(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity<BuyerResponseDTO> updateProfile(@AuthenticationPrincipal(expression = "user") User user,
                                                          @Valid @RequestBody UpdateBuyerProfileRequestDTO request) {
        return ResponseEntity.ok(ParticipantRestMapper.toResponse(
                buyerPort.updateMyProfile(user, ParticipantRestMapper.toDesiredBuyer(request))));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User user,
                                               @Valid @RequestBody ChangePasswordRequestDTO request) {
        buyerPort.changeMyPassword(user, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponseDTO> consultCart(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(buyerPort.consultMyCart(user)));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponseDTO> addCartItem(@AuthenticationPrincipal(expression = "user") User user,
                                                       @Valid @RequestBody CartItemRequestDTO request) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.addItemToCart(user, OrderRestMapper.toCartItem(request))));
    }

    @PatchMapping("/cart/items/{sku}")
    public ResponseEntity<CartResponseDTO> updateCartItem(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable String sku,
                                                          @RequestParam @Positive int quantity) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.updateCartItem(user, OrderRestMapper.toCartItem(sku, quantity))));
    }

    @DeleteMapping("/cart/items/{sku}")
    public ResponseEntity<CartResponseDTO> removeCartItem(@AuthenticationPrincipal(expression = "user") User user,
                                                          @PathVariable String sku) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.removeCartItem(user, OrderRestMapper.toCartItem(sku, 0))));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> checkout(@AuthenticationPrincipal(expression = "user") User user,
                                                     @Valid @RequestBody CheckoutRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderRestMapper.toResponse(buyerPort.checkout(user, request.shippingAddress())));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(buyerPort.consultMyOrders(user).stream().map(OrderRestMapper::toResponse).toList());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> consultOrder(@AuthenticationPrincipal(expression = "user") User user,
                                                         @PathVariable String orderId) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.consultMyOrder(user, OrderRestMapper.orderReference(orderId))));
    }

    @PostMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderResponseDTO> payOrder(@AuthenticationPrincipal(expression = "user") User user,
                                                     @PathVariable String orderId,
                                                     @Valid @RequestBody PayOrderRequestDTO request) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(buyerPort.payOrder(user,
                OrderRestMapper.orderReference(orderId), PaymentMethod.fromCode(request.paymentMethod()))));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@AuthenticationPrincipal(expression = "user") User user,
                                                        @PathVariable String orderId) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.cancelOrder(user, OrderRestMapper.orderReference(orderId))));
    }

    @GetMapping("/orders/{orderId}/shipment")
    public ResponseEntity<ShipmentResponseDTO> consultShipment(@AuthenticationPrincipal(expression = "user") User user,
                                                               @PathVariable String orderId) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.consultOrderShipment(user, OrderRestMapper.orderReference(orderId))));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> consultInvoices(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(buyerPort.consultMyInvoices(user).stream().map(OrderRestMapper::toResponse).toList());
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> consultInvoice(@AuthenticationPrincipal(expression = "user") User user,
                                                             @PathVariable String invoiceId) {
        return ResponseEntity.ok(OrderRestMapper.toResponse(
                buyerPort.consultMyInvoice(user, OrderRestMapper.invoiceReference(invoiceId))));
    }

    @PostMapping("/returns")
    public ResponseEntity<ReturnRequestResponseDTO> requestReturn(@AuthenticationPrincipal(expression = "user") User user,
                                                                  @Valid @RequestBody CreateReturnRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnRestMapper.toResponse(
                buyerPort.requestReturn(user, ReturnRestMapper.toDomain(request))));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnRequestResponseDTO>> consultReturns(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(buyerPort.consultMyReturns(user).stream().map(ReturnRestMapper::toResponse).toList());
    }
}
