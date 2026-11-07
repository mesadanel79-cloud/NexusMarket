package application.domain.models;

import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.OrderImmutableException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Purchase order of a buyer.
 *
 * <p>Lifecycle: {@code CART -> PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED};
 * digital-only orders go {@code PAID -> DELIVERED}; {@code CART | PENDING_PAYMENT ->
 * CANCELLED}. DELIVERED and CANCELLED orders are finalized and immutable.
 */
public class Order extends MarketplaceAsset {

    private Buyer buyer;
    private List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status;
    private Money totalAmount;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    /**
     * Drafts an order from the buyer's cart. Prices are frozen and the order starts in CART.
     */
    public static Order draftFrom(Cart cart, String shippingAddress) {
        if (cart == null || cart.isEmpty()) {
            throw new InvalidOrderException("Cannot create an order from an empty cart.");
        }
        Buyer buyer = cart.getBuyer();
        if (buyer == null || !buyer.canPurchase()) {
            throw new InvalidOrderException("Buyer is not allowed to place orders.");
        }
        Order order = new Order();
        order.buyer = buyer;
        int lineNumber = 1;
        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().isAvailableForSale()) {
                throw new InvalidOrderException("Product " + item.getProduct().getIdentifier()
                        + " is no longer available for purchase (not published or seller not active).");
            }
            OrderLine line = new OrderLine();
            line.setLineNumber(lineNumber++);
            line.setProduct(item.getProduct());
            line.setVariant(item.getVariant());
            line.setSeller(item.getProduct().getSeller());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getVariant().getPrice());
            order.lines.add(line);
        }
        order.totalAmount = order.calculateTotal();
        String address = shippingAddress == null || shippingAddress.isBlank() ? buyer.getAddress() : shippingAddress.trim();
        if (order.requiresShipping() && !buyer.isRegisteredAddress(address)) {
            throw new InvalidOrderException(
                    "Shipping address must be the buyer's main address or one of its additional addresses.");
        }
        order.shippingAddress = order.requiresShipping() ? address : null;
        order.status = OrderStatus.CART;
        order.createdAt = LocalDateTime.now();
        return order;
    }

    /** Assigns the warehouse from which a physical line was reserved (only while in CART). */
    public void allocateLine(int lineNumber, Warehouse warehouse) {
        ensureMutable();
        if (!OrderStatus.CART.equals(status)) {
            throw new InvalidOrderException("Lines can only be allocated while the order is in CART.");
        }
        OrderLine line = lines.stream().filter(l -> l.getLineNumber() == lineNumber).findFirst()
                .orElseThrow(() -> new InvalidOrderException("Order line " + lineNumber + " does not exist."));
        if (!line.isPhysical()) {
            throw new InvalidOrderException("Digital lines are not allocated to warehouses.");
        }
        line.setWarehouse(warehouse);
    }

    /** CART -> PENDING_PAYMENT. Every physical line must already have reserved stock. */
    public void submitForPayment() {
        ensureMutable();
        requireStatus(OrderStatus.CART, OrderStatus.PENDING_PAYMENT);
        boolean unallocated = lines.stream().anyMatch(l -> l.isPhysical() && l.getWarehouse() == null);
        if (unallocated) {
            throw new InvalidOrderException("All physical lines must have reserved stock before payment.");
        }
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    /** PENDING_PAYMENT -> PAID. */
    public void markPaid(PaymentMethod method) {
        ensureMutable();
        requireStatus(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        if (method == null) {
            throw new InvalidOrderException("Payment method is required.");
        }
        this.paymentMethod = method;
        this.status = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    /** PAID -> SHIPPED (only orders with physical products). */
    public void markShipped() {
        ensureMutable();
        requireStatus(OrderStatus.PAID, OrderStatus.SHIPPED);
        if (!requiresShipping()) {
            throw new InvalidOrderException("Digital-only orders are not shipped.");
        }
        this.status = OrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }

    /** SHIPPED -> DELIVERED, or PAID -> DELIVERED for digital-only orders. */
    public void markDelivered() {
        ensureMutable();
        boolean fromShipped = OrderStatus.SHIPPED.equals(status);
        boolean digitalFromPaid = OrderStatus.PAID.equals(status) && !requiresShipping();
        if (!fromShipped && !digitalFromPaid) {
            throw new InvalidStatusTransitionException("Order", code(), OrderStatus.DELIVERED.getCode());
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    /** CART | PENDING_PAYMENT -> CANCELLED. */
    /**
     * Cancels the order. Allowed while it is in CART, PENDING_PAYMENT or PAID (paid but not
     * shipped yet). Once SHIPPED the goods left the warehouse: the buyer must wait for the
     * delivery and request a return. DELIVERED and CANCELLED are final.
     */
    public void cancel() {
        ensureMutable();
        if (!OrderStatus.CART.equals(status) && !OrderStatus.PENDING_PAYMENT.equals(status)
                && !OrderStatus.PAID.equals(status)) {
            throw new InvalidStatusTransitionException("Order", code(), OrderStatus.CANCELLED.getCode());
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public boolean isFinalized() {
        return OrderStatus.DELIVERED.equals(status) || OrderStatus.CANCELLED.equals(status);
    }

    public boolean isPaymentExpired(LocalDateTime now, int expirationMinutes) {
        return OrderStatus.PENDING_PAYMENT.equals(status) && createdAt != null
                && !now.isBefore(createdAt.plusMinutes(expirationMinutes));
    }

    public boolean requiresShipping() {
        return lines.stream().anyMatch(OrderLine::isPhysical);
    }

    public boolean belongsTo(Buyer candidate) {
        return buyer != null && candidate != null && buyer.getIdentification() != null
                && buyer.getIdentification().equals(candidate.getIdentification());
    }

    public boolean containsProductsOf(Seller seller) {
        return lines.stream().anyMatch(l -> l.getSeller() != null && l.getSeller().sameAs(seller));
    }

    public Optional<OrderLine> findLine(String sku) {
        return lines.stream().filter(l -> sku != null && sku.equals(l.getSku())).findFirst();
    }

    private Money calculateTotal() {
        Money total = Money.zero(lines.get(0).getUnitPrice().getCurrency());
        for (OrderLine line : lines) {
            total = total.add(line.getLineTotal());
        }
        return total;
    }

    private void ensureMutable() {
        if (isFinalized()) {
            throw new OrderImmutableException("Order " + getIdentifier() + " is finalized (" + code()
                    + ") and cannot be modified.");
        }
    }

    private void requireStatus(OrderStatus expected, OrderStatus target) {
        if (!expected.equals(status)) {
            throw new InvalidStatusTransitionException("Order", code(), target.getCode());
        }
    }

    private String code() {
        return status == null ? "UNKNOWN" : status.getCode();
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public void setLines(List<OrderLine> lines) {
        this.lines = lines;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
