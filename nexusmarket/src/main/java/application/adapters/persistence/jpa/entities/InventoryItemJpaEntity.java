package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA persistence entity (repository DTO): Stock of a variant in a warehouse.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "inventory_items")
public class InventoryItemJpaEntity {

    @Id
    @Column(name = "identifier", length = 80, nullable = false)
    private String identifier;

    @Column(name = "warehouse_identifier", length = 20, nullable = false)
    private String warehouseIdentifier;

    @Column(name = "product_identifier", length = 30, nullable = false)
    private String productIdentifier;

    @Column(name = "sku", length = 40, nullable = false)
    private String sku;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "damaged_quantity", nullable = false)
    private Integer damagedQuantity;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getWarehouseIdentifier() {
        return warehouseIdentifier;
    }

    public void setWarehouseIdentifier(String warehouseIdentifier) {
        this.warehouseIdentifier = warehouseIdentifier;
    }

    public String getProductIdentifier() {
        return productIdentifier;
    }

    public void setProductIdentifier(String productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(Integer damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }
}
