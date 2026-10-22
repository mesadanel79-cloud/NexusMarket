package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA persistence entity (repository DTO): Inventory movements log.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "inventory_movements")
public class InventoryMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @Column(name = "inventory_item_identifier", length = 80, nullable = false)
    private String inventoryItemIdentifier;

    @Column(name = "movement_type", length = 30, nullable = false)
    private String movementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "performed_by_user_id")
    private Integer performedByUserId;

    @Column(name = "order_identifier", length = 30)
    private String orderIdentifier;

    @Column(name = "note", length = 255)
    private String note;

    public Long getMovementId() {
        return movementId;
    }

    public void setMovementId(Long movementId) {
        this.movementId = movementId;
    }

    public String getInventoryItemIdentifier() {
        return inventoryItemIdentifier;
    }

    public void setInventoryItemIdentifier(String inventoryItemIdentifier) {
        this.inventoryItemIdentifier = inventoryItemIdentifier;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Integer getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(Integer performedByUserId) {
        this.performedByUserId = performedByUserId;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
