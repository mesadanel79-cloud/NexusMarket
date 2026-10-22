package application.domain.models;

import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidInventoryOperationException;
import application.domain.exceptions.NegativeStockException;

/**
 * Stock of one product variant in one warehouse (distributed inventory).
 *
 * <p>Invariant: available, reserved and damaged quantities are never negative.
 * Only available units can be reserved: damaged units can never be sold. Every change
 * goes through a domain behavior that matches a {@code MovementType}.
 */
public class InventoryItem extends MarketplaceAsset {

    private Warehouse warehouse;
    private Product product;
    private ProductVariant variant;
    private int availableQuantity;
    private int reservedQuantity;
    private int damagedQuantity;

    public static String buildIdentifier(String warehouseIdentifier, String sku) {
        return "STK-" + warehouseIdentifier + "-" + sku;
    }

    /**
     * Opens a new stock record for a physical variant in an active warehouse.
     */
    public static InventoryItem open(Warehouse warehouse, Product product, ProductVariant variant) {
        if (warehouse == null || product == null || variant == null) {
            throw new InvalidInventoryOperationException("Warehouse, product and variant are required.");
        }
        warehouse.requireActive();
        if (!product.isPhysical()) {
            throw new InvalidInventoryOperationException("Digital products do not handle inventory.");
        }
        if (!warehouse.canStore(product)) {
            throw new InvalidInventoryOperationException("Warehouse " + warehouse.getIdentifier()
                    + " belongs to another seller and cannot store product " + product.getIdentifier() + ".");
        }
        InventoryItem item = new InventoryItem();
        item.setIdentifier(buildIdentifier(warehouse.getIdentifier(), variant.getSku()));
        item.warehouse = warehouse;
        item.product = product;
        item.variant = variant;
        item.availableQuantity = 0;
        item.reservedQuantity = 0;
        item.damagedQuantity = 0;
        return item;
    }

    /** INFLOW: stock received into the warehouse. */
    public void registerInflow(int quantity) {
        requirePositive(quantity);
        requireActiveWarehouse();
        availableQuantity += quantity;
    }

    /** RESERVATION: available stock reserved for an order pending payment. */
    public void reserve(int quantity) {
        requirePositive(quantity);
        requireActiveWarehouse();
        if (availableQuantity < quantity) {
            throw new InsufficientStockException("Insufficient stock for SKU " + sku() + " in warehouse "
                    + warehouseId() + ": available " + availableQuantity + ", requested " + quantity + ".");
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    /** RESERVATION_RELEASE: reserved stock returned to available. */
    public void releaseReservation(int quantity) {
        requirePositive(quantity);
        if (reservedQuantity < quantity) {
            throw new NegativeStockException("Cannot release " + quantity + " units of SKU " + sku()
                    + ": only " + reservedQuantity + " reserved.");
        }
        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }

    /** SALE: reserved stock consumed by a paid order. */
    public void confirmSale(int quantity) {
        requirePositive(quantity);
        if (reservedQuantity < quantity) {
            throw new NegativeStockException("Cannot sell " + quantity + " units of SKU " + sku()
                    + ": only " + reservedQuantity + " reserved.");
        }
        reservedQuantity -= quantity;
    }

    /** RETURN: returned units received back, as available stock or as damaged stock. */
    public void registerReturn(int quantity, boolean damaged) {
        requirePositive(quantity);
        if (damaged) {
            damagedQuantity += quantity;
        } else {
            availableQuantity += quantity;
        }
    }

    /** ADJUSTMENT: moves available units to damaged; damaged units cannot be reserved. */
    public void markDamaged(int quantity) {
        requirePositive(quantity);
        if (availableQuantity < quantity) {
            throw new NegativeStockException("Cannot mark " + quantity + " units as damaged: only "
                    + availableQuantity + " available.");
        }
        availableQuantity -= quantity;
        damagedQuantity += quantity;
    }

    /**
     * ADJUSTMENT: sets the available quantity after a physical count.
     *
     * @return the signed difference applied (new - previous)
     */
    public int adjustAvailableTo(int newAvailableQuantity) {
        if (newAvailableQuantity < 0) {
            throw new NegativeStockException("Available quantity cannot be negative.");
        }
        int delta = newAvailableQuantity - availableQuantity;
        if (delta == 0) {
            throw new InvalidInventoryOperationException("Adjustment does not change the available quantity.");
        }
        availableQuantity = newAvailableQuantity;
        return delta;
    }

    public int getTotalQuantity() {
        return availableQuantity + reservedQuantity + damagedQuantity;
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInventoryOperationException("Quantity must be greater than zero.");
        }
    }

    private void requireActiveWarehouse() {
        if (warehouse != null) {
            warehouse.requireActive();
        }
    }

    private String sku() {
        return variant == null ? "UNKNOWN" : variant.getSku();
    }

    private String warehouseId() {
        return warehouse == null ? "UNKNOWN" : warehouse.getIdentifier();
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public int getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(int damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }
}
