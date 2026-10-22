package application.domain.models;

import application.domain.exceptions.InvalidShipmentException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ShipmentStatus;
import java.time.LocalDateTime;

/**
 * Shipment of a paid order with physical products, handled by a logistic operator.
 */
public class Shipment extends MarketplaceAsset {

    private Order order;
    private User logisticOperator;
    private String carrier;
    private String trackingNumber;
    private String destinationAddress;
    private ShipmentStatus status;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;

    public static Shipment dispatch(Order order, User logisticOperator, String carrier, String trackingNumber) {
        if (order == null || !OrderStatus.PAID.equals(order.getStatus())) {
            throw new InvalidShipmentException("Only paid orders can be dispatched.");
        }
        if (!order.requiresShipping()) {
            throw new InvalidShipmentException("Digital-only orders do not require shipping.");
        }
        if (logisticOperator == null) {
            throw new InvalidShipmentException("Logistic operator is required.");
        }
        if (carrier == null || carrier.isBlank()) {
            throw new InvalidShipmentException("Carrier is required.");
        }
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new InvalidShipmentException("Tracking number is required.");
        }
        Shipment shipment = new Shipment();
        shipment.order = order;
        shipment.logisticOperator = logisticOperator;
        shipment.carrier = carrier;
        shipment.trackingNumber = trackingNumber;
        shipment.destinationAddress = order.getShippingAddress();
        shipment.status = ShipmentStatus.IN_TRANSIT;
        shipment.dispatchedAt = LocalDateTime.now();
        return shipment;
    }

    /** IN_TRANSIT -> DELIVERED. */
    public void confirmDelivery() {
        if (!ShipmentStatus.IN_TRANSIT.equals(status)) {
            throw new InvalidStatusTransitionException("Shipment",
                    status == null ? "UNKNOWN" : status.getCode(), ShipmentStatus.DELIVERED.getCode());
        }
        this.status = ShipmentStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getLogisticOperator() {
        return logisticOperator;
    }

    public void setLogisticOperator(User logisticOperator) {
        this.logisticOperator = logisticOperator;
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

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
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
