package application.domain.models;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

/**
 * Physical warehouse where the distributed inventory is stored.
 *
 * <p>A MARKETPLACE warehouse is operated by NexusMarket and may store products of any
 * seller; a SELLER warehouse belongs to one seller ({@code owner}) and stores only
 * that seller's products.
 */
public class Warehouse extends MarketplaceAsset {

    private String name;
    private String city;
    private String address;
    private WarehouseType warehouseType;
    private Seller owner;
    private WarehouseStatus status;

    public void register() {
        if (status != null) {
            throw new InvalidWarehouseException("Warehouse is already registered.");
        }
        if (getIdentifier() == null || !getIdentifier().matches("^[A-Z0-9-]{3,20}$")) {
            throw new InvalidWarehouseException(
                    "Warehouse code must have 3 to 20 characters (uppercase letters, digits or '-').");
        }
        requireText(name, "name");
        requireText(city, "city");
        requireText(address, "address");
        if (warehouseType == null) {
            throw new InvalidWarehouseException("Warehouse type is required.");
        }
        if (WarehouseType.SELLER.equals(warehouseType) && (owner == null || owner.getIdentification() == null)) {
            throw new InvalidWarehouseException("A seller warehouse requires its owner seller.");
        }
        if (WarehouseType.MARKETPLACE.equals(warehouseType) && owner != null) {
            throw new InvalidWarehouseException("A marketplace warehouse cannot have an owner seller.");
        }
        this.status = WarehouseStatus.ACTIVE;
    }

    public void activate() {
        if (!WarehouseStatus.INACTIVE.equals(status)) {
            throw new InvalidStatusTransitionException("Warehouse", code(), WarehouseStatus.ACTIVE.getCode());
        }
        this.status = WarehouseStatus.ACTIVE;
    }

    public void deactivate() {
        if (!WarehouseStatus.ACTIVE.equals(status)) {
            throw new InvalidStatusTransitionException("Warehouse", code(), WarehouseStatus.INACTIVE.getCode());
        }
        this.status = WarehouseStatus.INACTIVE;
    }

    public boolean isActive() {
        return WarehouseStatus.ACTIVE.equals(status);
    }

    public void requireActive() {
        if (!isActive()) {
            throw new InvalidWarehouseException("Warehouse " + getIdentifier() + " is not active.");
        }
    }

    /**
     * MARKETPLACE warehouses store any product; SELLER warehouses only their owner's products.
     */
    public boolean canStore(Product product) {
        if (product == null) {
            return false;
        }
        return WarehouseType.MARKETPLACE.equals(warehouseType) || product.isOwnedBy(owner);
    }

    public boolean isOwnedBy(Seller seller) {
        return owner != null && owner.sameAs(seller);
    }

    private String code() {
        return status == null ? "UNKNOWN" : status.getCode();
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidWarehouseException("Warehouse " + field + " is required.");
        }
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

    public WarehouseStatus getStatus() {
        return status;
    }

    public void setStatus(WarehouseStatus status) {
        this.status = status;
    }

    public WarehouseType getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(WarehouseType warehouseType) {
        this.warehouseType = warehouseType;
    }

    public Seller getOwner() {
        return owner;
    }

    public void setOwner(Seller owner) {
        this.owner = owner;
    }
}
