package application.domain.ports.in;

import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;
import application.domain.models.InventoryItem;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import java.util.List;

/**
 * Use cases of the SUPERVISOR role: read-only consultation and operational follow-up.
 */
public interface SupervisorPort {

    AdministrativeReport consultAdministrativeReport(User user);

    List<Order> consultAllOrders(User user);

    Order consultOrder(User user, Order order);

    List<Product> consultAllProducts(User user);

    List<Warehouse> consultWarehouses(User user);

    List<InventoryItem> consultWarehouseInventory(User user, Warehouse warehouse);

    List<ReturnRequest> consultReturns(User user, ReturnStatus status);

    List<Operation> consultOperations(User user);

    List<AuditLog> consultAuditLog(User user, OperationType operationType);
}
