package application.adapters.rest.mappers;

import static application.adapters.rest.mappers.UserRestMapper.code;

import application.adapters.rest.dtos.requests.CartItemRequestDTO;
import application.adapters.rest.dtos.responses.CartItemResponseDTO;
import application.adapters.rest.dtos.responses.CartResponseDTO;
import application.adapters.rest.dtos.responses.InvoiceResponseDTO;
import application.adapters.rest.dtos.responses.OrderLineResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.ProductVariant;
import application.domain.models.Shipment;
import application.domain.valueobjects.Money;
import java.util.List;

/**
 * Mapping of the purchase flow: cart, orders, invoices and shipments.
 */
public final class OrderRestMapper {

    private OrderRestMapper() {
    }

    public static CartItem toCartItem(CartItemRequestDTO dto) {
        return toCartItem(dto.sku(), dto.quantity() == null ? 0 : dto.quantity());
    }

    public static CartItem toCartItem(String sku, int quantity) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(sku);
        CartItem item = new CartItem();
        item.setVariant(variant);
        item.setQuantity(quantity);
        return item;
    }

    public static Order orderReference(String orderId) {
        Order order = new Order();
        order.setIdentifier(orderId);
        return order;
    }

    public static Invoice invoiceReference(String invoiceId) {
        Invoice invoice = new Invoice();
        invoice.setIdentifier(invoiceId);
        return invoice;
    }

    public static Shipment shipmentReference(String shipmentId) {
        Shipment shipment = new Shipment();
        shipment.setIdentifier(shipmentId);
        return shipment;
    }

    public static CartResponseDTO toResponse(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(i -> new CartItemResponseDTO(i.getProduct().getIdentifier(), i.getProduct().getName(), i.getSku(),
                        i.getQuantity(), i.getUnitPrice().getAmount(), i.getSubtotal().getAmount(),
                        i.getUnitPrice().getCurrency().getCode()))
                .toList();
        Money total = cart.getTotal();
        return new CartResponseDTO(cart.getCartId(), items, total == null ? null : total.getAmount(),
                total == null ? null : total.getCurrency().getCode(), cart.getUpdatedAt());
    }

    public static OrderResponseDTO toResponse(Order order) {
        List<OrderLineResponseDTO> lines = order.getLines().stream()
                .map(l -> new OrderLineResponseDTO(l.getLineNumber(), l.getProduct().getIdentifier(),
                        l.getProduct().getName(), code(l.getProduct().getProductType()), l.getSku(),
                        l.getSeller() == null ? null : l.getSeller().getIdentification(), l.getQuantity(),
                        l.getUnitPrice().getAmount(), l.getLineTotal().getAmount(),
                        l.getWarehouse() == null ? null : l.getWarehouse().getIdentifier()))
                .toList();
        return new OrderResponseDTO(order.getIdentifier(),
                order.getBuyer() == null ? null : order.getBuyer().getIdentification(), code(order.getStatus()),
                order.getTotalAmount().getAmount(), order.getTotalAmount().getCurrency().getCode(),
                order.getShippingAddress(), code(order.getPaymentMethod()), order.getCreatedAt(), order.getPaidAt(),
                order.getShippedAt(), order.getDeliveredAt(), order.getCancelledAt(), lines);
    }

    public static InvoiceResponseDTO toResponse(Invoice invoice) {
        return new InvoiceResponseDTO(invoice.getIdentifier(),
                invoice.getOrder() == null ? null : invoice.getOrder().getIdentifier(),
                invoice.getBuyer() == null ? null : invoice.getBuyer().getIdentification(), invoice.getIssueDate(),
                invoice.getSubtotal().getAmount(), invoice.getTaxAmount().getAmount(), invoice.getTotal().getAmount(),
                invoice.getRefundedAmount().getAmount(), invoice.getTotal().getCurrency().getCode(),
                code(invoice.getStatus()));
    }

    public static ShipmentResponseDTO toResponse(Shipment shipment) {
        return new ShipmentResponseDTO(shipment.getIdentifier(),
                shipment.getOrder() == null ? null : shipment.getOrder().getIdentifier(),
                shipment.getLogisticOperator() == null ? null : shipment.getLogisticOperator().getUserId(),
                shipment.getCarrier(), shipment.getTrackingNumber(), shipment.getDestinationAddress(),
                code(shipment.getStatus()), shipment.getDispatchedAt(), shipment.getDeliveredAt());
    }
}
