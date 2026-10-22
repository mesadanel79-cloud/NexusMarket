# Input Ports (Puertos de Entrada por Rol)

Los puertos de entrada (`application.domain.ports.in`) están organizados **exclusivamente por rol de usuario**, más un puerto de acceso público, siguiendo el patrón del proyecto base. Reciben y devuelven **modelos de dominio** y Value Objects; el `User` que reciben es el reconstruido desde el JWT. Sus implementaciones están en `adapters/useCases/*UseCaseImpl` e inyectan los servicios de dominio.

| Puerto | Rol | Prefijo REST |
| --- | --- | --- |
| `PublicAccessPort` | Público (login, registro de comprador, catálogo) | `/api/v1/auth`, `/api/v1/catalog` |
| `BuyerPort` | `BUYER` | `/api/v1/buyer` |
| `SellerPort` | `SELLER` | `/api/v1/seller` |
| `LogisticOperatorPort` | `LOGISTIC_OPERATOR` | `/api/v1/logistics` |
| `SupervisorPort` | `SUPERVISOR` (solo consulta) | `/api/v1/supervisor` |
| `AdminPort` | `ADMIN` | `/api/v1/admin` |

> Nota: la rúbrica (`enunciado evaluativo.md`) lista por error los puertos del proyecto bancario; aquí se aplica la misma regla ("organizados exclusivamente por rol") a los roles reales de NexusMarket.


## AdminPort

```java
/**
 * Use cases of the ADMIN role: users, buyers, sellers, warehouses, returns and refunds.
 */
public interface AdminPort {

    User registerSeller(User admin, User newSellerUser, Warehouse firstWarehouse);

    User registerStaffUser(User admin, User newStaffUser);

    List<User> consultUsers(User admin);

    User changeUserStatus(User admin, User target, UserStatus newStatus);

    Buyer changeBuyerStatus(User admin, Buyer buyer, BuyerStatus newStatus);

    List<Seller> consultSellers(User admin);

    Seller changeSellerStatus(User admin, Seller seller, SellerStatus newStatus);

    Warehouse registerWarehouse(User admin, Warehouse warehouse);

    List<Warehouse> consultWarehouses(User admin);

    Warehouse changeWarehouseStatus(User admin, Warehouse warehouse, WarehouseStatus newStatus);

    List<ReturnRequest> consultReturns(User admin, ReturnStatus status);

    ReturnRequest reviewReturn(User admin, ReturnRequest returnRequest, ApprovalDecision decision, String rejectionReason);

    ReturnRequest issueRefund(User admin, ReturnRequest returnRequest);

    AdministrativeReport consultAdministrativeReport(User admin);

    List<AuditLog> consultAuditLog(User admin, OperationType operationType);
}
```

## BuyerPort

```java
/**
 * Use cases of the BUYER role.
 */
public interface BuyerPort {

    Buyer consultMyProfile(User user);

    Buyer updateMyProfile(User user, Buyer buyer);

    void changeMyPassword(User user, String currentPassword, String newPassword);

    Cart consultMyCart(User user);

    Cart addItemToCart(User user, CartItem item);

    Cart updateCartItem(User user, CartItem item);

    Cart removeCartItem(User user, CartItem item);

    Order checkout(User user, String shippingAddress);

    Order payOrder(User user, Order order, PaymentMethod paymentMethod);

    Order cancelOrder(User user, Order order);

    List<Order> consultMyOrders(User user);

    Order consultMyOrder(User user, Order order);

    Shipment consultOrderShipment(User user, Order order);

    List<Invoice> consultMyInvoices(User user);

    Invoice consultMyInvoice(User user, Invoice invoice);

    ReturnRequest requestReturn(User user, ReturnRequest returnRequest);

    List<ReturnRequest> consultMyReturns(User user);
}
```

## LogisticOperatorPort

```java
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
```

## PublicAccessPort

```java
/**
 * Operations available without authentication (plus logout).
 */
public interface PublicAccessPort {

    AuthenticationResult login(User credentials);

    void logout(User user);

    User registerBuyer(User newBuyerUser);

    List<Product> consultCatalog(ProductCategory category);

    Product consultProduct(Product product);
}
```

## SellerPort

```java
/**
 * Use cases of the SELLER role.
 */
public interface SellerPort {

    Seller consultMyStore(User user);

    void changeMyPassword(User user, String currentPassword, String newPassword);

    Product createProduct(User user, Product product);

    List<Product> consultMyProducts(User user);

    Product addVariant(User user, Product product, ProductVariant variant);

    Product changeVariantPrice(User user, Product product, String sku, Money newPrice);

    Product suspendProduct(User user, Product product);

    Product reactivateProduct(User user, Product product);

    Product discontinueProduct(User user, Product product);

    List<InventoryItem> consultProductInventory(User user, Product product);

    InventoryItem registerInventoryInflow(User user, Warehouse warehouse, ProductVariant variant, int quantity);

    InventoryItem adjustInventory(User user, InventoryItem item, int newAvailableQuantity, String reason);

    InventoryItem markDamagedInventory(User user, InventoryItem item, int quantity, String reason);

    List<Order> consultMySales(User user);
}
```

## SupervisorPort

```java
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
```
