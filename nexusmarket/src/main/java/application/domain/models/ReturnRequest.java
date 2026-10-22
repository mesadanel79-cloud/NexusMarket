package application.domain.models;

import application.domain.exceptions.InvalidReturnException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnReason;
import application.domain.valueobjects.ReturnStatus;
import java.time.LocalDateTime;

/**
 * Return of units of one order line, followed by its refund.
 *
 * <p>Lifecycle: {@code REQUESTED -> APPROVED | REJECTED}; {@code APPROVED -> RECEIVED};
 * {@code RECEIVED -> REFUNDED}. The original order is never modified (it is finalized).
 */
public class ReturnRequest extends MarketplaceAsset {

    private Order order;
    private Buyer buyer;
    private String sku;
    private int quantity;
    private ReturnReason reason;
    private String comment;
    private ReturnStatus status;
    private LocalDateTime requestedAt;
    private User reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private User receivedBy;
    private LocalDateTime receivedAt;
    private Refund refund;

    /**
     * Creates a return request validating ownership, order status, product type,
     * quantity already returned and the return window.
     */
    public static ReturnRequest request(Order order, Buyer buyer, String sku, int quantity, ReturnReason reason,
                                        String comment, int alreadyReturnedQuantity, int returnWindowDays) {
        if (order == null || buyer == null) {
            throw new InvalidReturnException("Order and buyer are required.");
        }
        if (!order.belongsTo(buyer)) {
            throw new InvalidReturnException("The order does not belong to the buyer.");
        }
        if (!OrderStatus.DELIVERED.equals(order.getStatus())) {
            throw new InvalidReturnException("Only delivered orders can be returned.");
        }
        OrderLine line = order.findLine(sku)
                .orElseThrow(() -> new InvalidReturnException("SKU " + sku + " is not part of the order."));
        if (!line.isPhysical()) {
            throw new InvalidReturnException("Digital products cannot be returned.");
        }
        if (quantity <= 0) {
            throw new InvalidReturnException("Return quantity must be greater than zero.");
        }
        if (quantity + alreadyReturnedQuantity > line.getQuantity()) {
            throw new InvalidReturnException("Return quantity exceeds the purchased quantity not yet returned.");
        }
        if (reason == null) {
            throw new InvalidReturnException("Return reason is required.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (order.getDeliveredAt() == null || now.isAfter(order.getDeliveredAt().plusDays(returnWindowDays))) {
            throw new InvalidReturnException("The return window of " + returnWindowDays + " days has expired.");
        }
        ReturnRequest request = new ReturnRequest();
        request.order = order;
        request.buyer = buyer;
        request.sku = sku;
        request.quantity = quantity;
        request.reason = reason;
        request.comment = comment;
        request.status = ReturnStatus.REQUESTED;
        request.requestedAt = now;
        return request;
    }

    /** REQUESTED -> APPROVED. */
    public void approve(User reviewer) {
        requireStatus(ReturnStatus.REQUESTED, ReturnStatus.APPROVED);
        requireUser(reviewer);
        this.status = ReturnStatus.APPROVED;
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }

    /** REQUESTED -> REJECTED. */
    public void reject(User reviewer, String reason) {
        requireStatus(ReturnStatus.REQUESTED, ReturnStatus.REJECTED);
        requireUser(reviewer);
        if (reason == null || reason.isBlank()) {
            throw new InvalidReturnException("A rejection reason is required.");
        }
        this.status = ReturnStatus.REJECTED;
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /** APPROVED -> RECEIVED. */
    public void markReceived(User operator) {
        requireStatus(ReturnStatus.APPROVED, ReturnStatus.RECEIVED);
        requireUser(operator);
        this.status = ReturnStatus.RECEIVED;
        this.receivedBy = operator;
        this.receivedAt = LocalDateTime.now();
    }

    /** RECEIVED -> REFUNDED. The refund amount is unit price x returned quantity. */
    public Refund issueRefund(User issuer) {
        requireStatus(ReturnStatus.RECEIVED, ReturnStatus.REFUNDED);
        requireUser(issuer);
        Refund newRefund = new Refund();
        newRefund.setAmount(getRefundAmount());
        newRefund.setIssuedAt(LocalDateTime.now());
        newRefund.setIssuedBy(issuer);
        this.refund = newRefund;
        this.status = ReturnStatus.REFUNDED;
        return newRefund;
    }

    public OrderLine getOrderLine() {
        return order.findLine(sku)
                .orElseThrow(() -> new InvalidReturnException("SKU " + sku + " is not part of the order."));
    }

    public Money getRefundAmount() {
        return getOrderLine().getUnitPrice().multiply(quantity);
    }

    /** True while the request still counts against the purchased quantity. */
    public boolean countsAsReturned() {
        return !ReturnStatus.REJECTED.equals(status);
    }

    private void requireStatus(ReturnStatus expected, ReturnStatus target) {
        if (!expected.equals(status)) {
            throw new InvalidStatusTransitionException("Return",
                    status == null ? "UNKNOWN" : status.getCode(), target.getCode());
        }
    }

    private static void requireUser(User user) {
        if (user == null) {
            throw new InvalidReturnException("Acting user is required.");
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ReturnReason getReason() {
        return reason;
    }

    public void setReason(ReturnReason reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public User getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(User receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }
}
