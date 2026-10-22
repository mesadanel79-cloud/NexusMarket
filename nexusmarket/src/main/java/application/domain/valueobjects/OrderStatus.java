package application.domain.valueobjects;

import java.util.List;

/**
 * Lifecycle status of an order.
 */
public final class OrderStatus extends DomainCatalog {
    public static final OrderStatus CART = new OrderStatus(
            "CART", "Cart", "Provisional selection of products taken from the buyer's cart; stock not reserved yet.");
    public static final OrderStatus PENDING_PAYMENT = new OrderStatus(
            "PENDING_PAYMENT", "Pending Payment", "Stock reserved; waiting for payment.");
    public static final OrderStatus PAID = new OrderStatus(
            "PAID", "Paid", "Payment confirmed and invoice issued; picking and packing starts.");
    public static final OrderStatus SHIPPED = new OrderStatus(
            "SHIPPED", "Shipped", "Order physically left the warehouse.");
    public static final OrderStatus DELIVERED = new OrderStatus(
            "DELIVERED", "Delivered", "Order delivered. Final and immutable.");
    public static final OrderStatus CANCELLED = new OrderStatus(
            "CANCELLED", "Cancelled", "Order cancelled before payment. Final and immutable.");

    private static final List<OrderStatus> VALUES = List.of(
            CART, PENDING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELLED);

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<OrderStatus> values() {
        return VALUES;
    }

    public static OrderStatus fromCode(String code) {
        return resolve(VALUES, code, "OrderStatus");
    }
}
