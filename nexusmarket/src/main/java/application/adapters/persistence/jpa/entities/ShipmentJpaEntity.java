package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA persistence entity (repository DTO): Shipments of orders.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "shipments")
public class ShipmentJpaEntity {

    @Id
    @Column(name = "identifier", length = 30, nullable = false)
    private String identifier;

    @Column(name = "order_identifier", length = 30, nullable = false, unique = true)
    private String orderIdentifier;

    @Column(name = "logistic_operator_user_id", nullable = false)
    private Integer logisticOperatorUserId;

    @Column(name = "carrier", length = 60, nullable = false)
    private String carrier;

    @Column(name = "tracking_number", length = 60, nullable = false)
    private String trackingNumber;

    @Column(name = "destination_address", length = 200)
    private String destinationAddress;

    @Column(name = "status", length = 15, nullable = false)
    private String status;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

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

    public Integer getLogisticOperatorUserId() {
        return logisticOperatorUserId;
    }

    public void setLogisticOperatorUserId(Integer logisticOperatorUserId) {
        this.logisticOperatorUserId = logisticOperatorUserId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(LocalDateTime dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
