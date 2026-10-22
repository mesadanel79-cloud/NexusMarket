package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA persistence entity (repository DTO): Warehouses of the distributed inventory.
 * Catalog Value Objects are stored by their code; other aggregates by reference identifier.
 */
@Entity
@Table(name = "warehouses")
public class WarehouseJpaEntity {

    @Id
    @Column(name = "identifier", length = 20, nullable = false)
    private String identifier;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "city", length = 60, nullable = false)
    private String city;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @Column(name = "warehouse_type", length = 15, nullable = false)
    private String warehouseType;

    @Column(name = "owner_seller_identification", length = 20)
    private String ownerSellerIdentification;

    @Column(name = "status", length = 15, nullable = false)
    private String status;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

    public String getOwnerSellerIdentification() {
        return ownerSellerIdentification;
    }

    public void setOwnerSellerIdentification(String ownerSellerIdentification) {
        this.ownerSellerIdentification = ownerSellerIdentification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
