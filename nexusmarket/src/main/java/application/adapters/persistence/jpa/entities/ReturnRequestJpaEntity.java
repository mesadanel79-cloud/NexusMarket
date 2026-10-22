package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA persistence entity (repository DTO): Return requests and their refunds.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "return_requests")
public class ReturnRequestJpaEntity {

    @Id
    @Column(name = "identifier", length = 30, nullable = false)
    private String identifier;

    @Column(name = "order_identifier", length = 30, nullable = false)
    private String orderIdentifier;

    @Column(name = "buyer_identification", length = 20, nullable = false)
    private String buyerIdentification;

    @Column(name = "sku", length = 40, nullable = false)
    private String sku;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reason", length = 30, nullable = false)
    private String reason;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "status", length = 15, nullable = false)
    private String status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "reviewed_by_user_id")
    private Integer reviewedByUserId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "received_by_user_id")
    private Integer receivedByUserId;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "refund_identifier", length = 40)
    private String refundIdentifier;

    @Column(name = "refund_amount", precision = 19, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_currency", length = 5)
    private String refundCurrency;

    @Column(name = "refund_issued_at")
    private LocalDateTime refundIssuedAt;

    @Column(name = "refund_issued_by_user_id")
    private Integer refundIssuedByUserId;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public String getBuyerIdentification() {
        return buyerIdentification;
    }

    public void setBuyerIdentification(String buyerIdentification) {
        this.buyerIdentification = buyerIdentification;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Integer getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(Integer reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
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

    public Integer getReceivedByUserId() {
        return receivedByUserId;
    }

    public void setReceivedByUserId(Integer receivedByUserId) {
        this.receivedByUserId = receivedByUserId;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getRefundIdentifier() {
        return refundIdentifier;
    }

    public void setRefundIdentifier(String refundIdentifier) {
        this.refundIdentifier = refundIdentifier;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundCurrency() {
        return refundCurrency;
    }

    public void setRefundCurrency(String refundCurrency) {
        this.refundCurrency = refundCurrency;
    }

    public LocalDateTime getRefundIssuedAt() {
        return refundIssuedAt;
    }

    public void setRefundIssuedAt(LocalDateTime refundIssuedAt) {
        this.refundIssuedAt = refundIssuedAt;
    }

    public Integer getRefundIssuedByUserId() {
        return refundIssuedByUserId;
    }

    public void setRefundIssuedByUserId(Integer refundIssuedByUserId) {
        this.refundIssuedByUserId = refundIssuedByUserId;
    }
}
