package application.domain.models;

import application.domain.exceptions.InvalidInventoryOperationException;
import application.domain.valueobjects.MovementType;
import java.time.LocalDateTime;

/**
 * Immutable record of a change applied to an {@link InventoryItem}.
 *
 * <p>{@code quantity} is positive for every type except ADJUSTMENT, where it holds
 * the signed difference applied to the available stock.
 */
public class InventoryMovement {

    private Long movementId;
    private InventoryItem inventoryItem;
    private MovementType movementType;
    private int quantity;
    private LocalDateTime occurredAt;
    private User performedBy;
    private Order order;
    private String note;

    public static InventoryMovement record(InventoryItem item, MovementType type, int quantity,
                                           User performedBy, Order order, String note) {
        if (item == null) {
            throw new InvalidInventoryOperationException("Inventory item is required for a movement.");
        }
        if (type == null) {
            throw new InvalidInventoryOperationException("Movement type is required.");
        }
        if (quantity == 0 || (quantity < 0 && !MovementType.ADJUSTMENT.equals(type))) {
            throw new InvalidInventoryOperationException("Movement quantity is invalid for type " + type.getCode() + ".");
        }
        InventoryMovement movement = new InventoryMovement();
        movement.inventoryItem = item;
        movement.movementType = type;
        movement.quantity = quantity;
        movement.performedBy = performedBy;
        movement.order = order;
        movement.note = note;
        movement.occurredAt = LocalDateTime.now();
        return movement;
    }

    public Long getMovementId() {
        return movementId;
    }

    public void setMovementId(Long movementId) {
        this.movementId = movementId;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
