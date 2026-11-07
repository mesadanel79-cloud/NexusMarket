package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.OrderLine;
import application.domain.models.ProductVariant;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.ReturnReason;
import java.util.Comparator;

/**
 * Applies the stock effects of the order and return flows over the distributed
 * inventory and records one {@link InventoryMovement} per change.
 *
 * <p>Allocation rule: each physical order line is reserved completely from the
 * ACTIVE warehouse with the highest available quantity that can cover it.
 */
public class InventoryAllocationService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;

    public InventoryAllocationService(InventoryRepositoryPort inventoryRepositoryPort,
                                      InventoryMovementRepositoryPort inventoryMovementRepositoryPort) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.inventoryMovementRepositoryPort = inventoryMovementRepositoryPort;
    }

    /** Returns the stock available for a SKU across active warehouses. */
    public int availableQuantity(ProductVariant variant) {
        return inventoryRepositoryPort.findByVariant(variant).stream()
                .filter(item -> item.getWarehouse() != null && item.getWarehouse().isActive())
                .mapToInt(InventoryItem::getAvailableQuantity)
                .sum();
    }

    /** RESERVATION for every physical line of an order in CART. */
    /**
     * Largest quantity that a single active warehouse can supply. Checkout reserves every
     * order line from one warehouse, so the cart validates against this same criterion.
     */
    public int availableInSingleWarehouse(ProductVariant variant) {
        return inventoryRepositoryPort.findByVariant(variant).stream()
                .filter(item -> item.getWarehouse() != null && item.getWarehouse().isActive())
                .mapToInt(InventoryItem::getAvailableQuantity)
                .max()
                .orElse(0);
    }

    public void reserve(Order order, User performedBy) {
        for (OrderLine line : order.getLines()) {
            if (!line.isPhysical()) {
                continue;
            }
            InventoryItem item = inventoryRepositoryPort.findByVariant(line.getVariant()).stream()
                    .filter(candidate -> candidate.getWarehouse() != null && candidate.getWarehouse().isActive())
                    .filter(candidate -> candidate.getAvailableQuantity() >= line.getQuantity())
                    .max(Comparator.comparingInt(InventoryItem::getAvailableQuantity))
                    .orElseThrow(() -> new InsufficientStockException("No active warehouse has "
                            + line.getQuantity() + " units available of SKU " + line.getSku() + "."));
            item.reserve(line.getQuantity());
            inventoryRepositoryPort.update(item);
            order.allocateLine(line.getLineNumber(), item.getWarehouse());
            record(item, MovementType.RESERVATION, line.getQuantity(), performedBy, order, null);
        }
    }

    /** RESERVATION_RELEASE for an order that is cancelled or expires. */
    public void release(Order order, User performedBy, String note) {
        for (OrderLine line : order.getLines()) {
            if (!line.isPhysical() || line.getWarehouse() == null) {
                continue;
            }
            InventoryItem item = load(line);
            item.releaseReservation(line.getQuantity());
            inventoryRepositoryPort.update(item);
            record(item, MovementType.RESERVATION_RELEASE, line.getQuantity(), performedBy, order, note);
        }
    }

    /** SALE: consumes the reserved stock of a paid order. */
    public void confirmSale(Order order, User performedBy) {
        for (OrderLine line : order.getLines()) {
            if (!line.isPhysical()) {
                continue;
            }
            InventoryItem item = load(line);
            item.confirmSale(line.getQuantity());
            inventoryRepositoryPort.update(item);
            record(item, MovementType.SALE, line.getQuantity(), performedBy, order, null);
        }
    }

    /** RETURN: puts returned units back into the warehouse they were sold from (damaged if DEFECTIVE). */
    /**
     * Reverses the SALE of a paid order cancelled before shipping: the units never left the
     * warehouse, so they go back to available stock (movement RETURN).
     */
    public void reverseSale(Order order, User performedBy, String note) {
        for (OrderLine line : order.getLines()) {
            if (!line.isPhysical() || line.getWarehouse() == null) {
                continue;
            }
            InventoryItem item = load(line);
            item.registerReturn(line.getQuantity(), false);
            inventoryRepositoryPort.update(item);
            record(item, MovementType.RETURN, line.getQuantity(), performedBy, order, note);
        }
    }

    public InventoryItem restock(ReturnRequest returnRequest, User performedBy) {
        OrderLine line = returnRequest.getOrderLine();
        InventoryItem item = load(line);
        boolean damaged = ReturnReason.DEFECTIVE.equals(returnRequest.getReason());
        item.registerReturn(returnRequest.getQuantity(), damaged);
        inventoryRepositoryPort.update(item);
        record(item, MovementType.RETURN, returnRequest.getQuantity(), performedBy, returnRequest.getOrder(),
                "Return " + returnRequest.getIdentifier() + (damaged ? " (damaged)" : ""));
        return item;
    }

    private InventoryItem load(OrderLine line) {
        InventoryItem probe = new InventoryItem();
        probe.setIdentifier(InventoryItem.buildIdentifier(line.getWarehouse().getIdentifier(), line.getSku()));
        return inventoryRepositoryPort.findByIdentifier(probe)
                .orElseThrow(() -> new EntityNotFoundException("InventoryItem", probe.getIdentifier()));
    }

    private void record(InventoryItem item, MovementType type, int quantity, User user, Order order, String note) {
        inventoryMovementRepositoryPort.save(InventoryMovement.record(item, type, quantity, user, order, note));
    }
}
