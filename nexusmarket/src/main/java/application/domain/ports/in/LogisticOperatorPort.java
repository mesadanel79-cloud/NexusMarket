package application.domain.ports.in;

import application.domain.models.InventoryItem;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.ProductVariant;
import application.domain.models.ReturnRequest;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ShipmentStatus;
import java.util.List;

/**
 * Use cases of the LOGISTIC_OPERATOR role.
 */
public interface LogisticOperatorPort {

    List<Warehouse> consultWarehouses(User user);

    List<InventoryItem> consultWarehouseInventory(User user, Warehouse warehouse);

    InventoryItem registerInventoryInflow(User user, Warehouse warehouse, ProductVariant variant, int quantity);

    InventoryItem adjustInventory(User user, InventoryItem item, int newAvailableQuantity, String reason);

    InventoryItem markDamagedInventory(User user, InventoryItem item, int quantity, String reason);

    List<InventoryMovement> consultInventoryMovements(User user, InventoryItem item);

    List<Order> consultOrdersReadyToShip(User user);

    Shipment dispatchOrder(User user, Order order, String carrier, String trackingNumber);

    Shipment confirmDelivery(User user, Shipment shipment);

    List<Shipment> consultShipments(User user, ShipmentStatus status);

    List<ReturnRequest> consultApprovedReturns(User user);

    ReturnRequest receiveReturn(User user, ReturnRequest returnRequest);
}
