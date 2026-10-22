# Output Ports (Puertos de Salida)

Los puertos de salida (`application.domain.ports.out`) son las interfaces que el dominio necesita del exterior. Reciben modelos de dominio como criterio de búsqueda (patrón del proyecto base) y son implementados por los adaptadores:

| Puerto | Adaptador | Tecnología |
| --- | --- | --- |
| `UserRepositoryPort` | `UserJpaAdapter` | MySQL / JPA |
| `BuyerRepositoryPort` | `BuyerJpaAdapter` | MySQL / JPA |
| `SellerRepositoryPort` | `SellerJpaAdapter` | MySQL / JPA |
| `WarehouseRepositoryPort` | `WarehouseJpaAdapter` | MySQL / JPA |
| `ProductRepositoryPort` | `ProductJpaAdapter` | MySQL / JPA |
| `InventoryRepositoryPort`, `InventoryMovementRepositoryPort` | `InventoryJpaAdapter` | MySQL / JPA |
| `CartRepositoryPort` | `CartJpaAdapter` | MySQL / JPA |
| `OrderRepositoryPort` | `OrderJpaAdapter` | MySQL / JPA |
| `InvoiceRepositoryPort` | `InvoiceJpaAdapter` | MySQL / JPA |
| `ShipmentRepositoryPort` | `ShipmentJpaAdapter` | MySQL / JPA |
| `ReturnRequestRepositoryPort` | `ReturnRequestJpaAdapter` | MySQL / JPA |
| `OperationRepositoryPort` | `OperationJpaAdapter` | MySQL / JPA |
| `AuditLogRepositoryPort` | `AuditLogMongoAdapter` | MongoDB |
| `PasswordServicePort` | `BCryptPasswordServiceAdapter` | Spring Security BCrypt |
| `JwtServicePort` | `JwtProvider` | JJWT (HS256) |
| `BusinessConfigurationPort` | `DefaultBusinessConfigurationAdapter` | `application.properties` |


## AuditLogRepositoryPort

```java
public interface AuditLogRepositoryPort {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByUser(User user);

    List<AuditLog> findByAsset(MarketplaceAsset asset);

    List<AuditLog> findByOperationType(OperationType operationType);

    List<AuditLog> findAll();
}
```

## BusinessConfigurationPort

```java
/**
 * Configurable business parameters of the marketplace.
 */
public interface BusinessConfigurationPort {

    /** Tax rate included in prices (e.g. 0.19). */
    BigDecimal getTaxRate();

    /** Days after delivery during which a return can be requested. */
    int getReturnWindowDays();

    /** Minutes an order may stay in PENDING_PAYMENT before it expires. */
    int getPaymentExpirationMinutes();

    /** Available quantity at or below which a stock record is reported as low stock. */
    int getLowStockThreshold();
}
```

## BuyerRepositoryPort

```java
public interface BuyerRepositoryPort {

    Buyer save(Buyer buyer);

    void update(Buyer buyer);

    Optional<Buyer> findByIdentification(Buyer buyer);

    boolean existsByIdentification(Buyer buyer);

    boolean existsByEmail(Buyer buyer);
}
```

## CartRepositoryPort

```java
public interface CartRepositoryPort {

    Cart save(Cart cart);

    Optional<Cart> findByBuyer(Buyer buyer);
}
```

## InventoryMovementRepositoryPort

```java
public interface InventoryMovementRepositoryPort {

    InventoryMovement save(InventoryMovement movement);

    List<InventoryMovement> findByInventoryItem(InventoryItem item);
}
```

## InventoryRepositoryPort

```java
public interface InventoryRepositoryPort {

    InventoryItem save(InventoryItem item);

    void update(InventoryItem item);

    Optional<InventoryItem> findByIdentifier(InventoryItem item);

    List<InventoryItem> findByVariant(ProductVariant variant);

    List<InventoryItem> findByWarehouse(Warehouse warehouse);

    List<InventoryItem> findByProduct(Product product);

    List<InventoryItem> findAll();
}
```

## InvoiceRepositoryPort

```java
public interface InvoiceRepositoryPort {

    Invoice save(Invoice invoice);

    void update(Invoice invoice);

    Optional<Invoice> findByIdentifier(Invoice invoice);

    Optional<Invoice> findByOrder(Order order);

    List<Invoice> findByBuyer(Buyer buyer);

    List<Invoice> findAll();
}
```

## JwtServicePort

```java
/**
 * Issues and reads identity tokens. The token carries the claims needed to rebuild
 * the {@link User} Domain Model ({@code sub}, {@code ver}, role, status, profile ids).
 */
public interface JwtServicePort {

    String generateToken(User user);

    boolean isValid(String token);

    User reconstructUser(String token);
}
```

## OperationRepositoryPort

```java
public interface OperationRepositoryPort {

    Operation save(Operation operation);

    List<Operation> findByUser(User user);

    List<Operation> findByAsset(MarketplaceAsset asset);

    List<Operation> findAll();
}
```

## OrderRepositoryPort

```java
public interface OrderRepositoryPort {

    Order save(Order order);

    void update(Order order);

    Optional<Order> findByIdentifier(Order order);

    List<Order> findByBuyer(Buyer buyer);

    List<Order> findBySeller(Seller seller);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();
}
```

## PasswordServicePort

```java
package application.domain.ports.out;

public interface PasswordServicePort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
```

## ProductRepositoryPort

```java
public interface ProductRepositoryPort {

    Product save(Product product);

    void update(Product product);

    Optional<Product> findByIdentifier(Product product);

    Optional<Product> findByVariantSku(ProductVariant variant);

    boolean existsBySku(ProductVariant variant);

    List<Product> findBySeller(Seller seller);

    List<Product> findPublished(ProductCategory category);

    List<Product> findAll();
}
```

## ReturnRequestRepositoryPort

```java
public interface ReturnRequestRepositoryPort {

    ReturnRequest save(ReturnRequest returnRequest);

    void update(ReturnRequest returnRequest);

    Optional<ReturnRequest> findByIdentifier(ReturnRequest returnRequest);

    List<ReturnRequest> findByOrder(Order order);

    List<ReturnRequest> findByBuyer(Buyer buyer);

    List<ReturnRequest> findByStatus(ReturnStatus status);

    List<ReturnRequest> findAll();
}
```

## SellerRepositoryPort

```java
public interface SellerRepositoryPort {

    Seller save(Seller seller);

    void update(Seller seller);

    Optional<Seller> findByIdentification(Seller seller);

    boolean existsByIdentification(Seller seller);

    boolean existsByEmail(Seller seller);

    List<Seller> findAll();
}
```

## ShipmentRepositoryPort

```java
public interface ShipmentRepositoryPort {

    Shipment save(Shipment shipment);

    void update(Shipment shipment);

    Optional<Shipment> findByIdentifier(Shipment shipment);

    Optional<Shipment> findByOrder(Order order);

    List<Shipment> findByStatus(ShipmentStatus status);
}
```

## UserRepositoryPort

```java
public interface UserRepositoryPort {

    User save(User user);

    void update(User user);

    Optional<User> findById(User user);

    Optional<User> findByUsername(User user);

    boolean existsByUsername(User user);

    boolean existsByIdentification(User user);

    boolean existsByEmail(User user);

    List<User> findAll();
}
```

## WarehouseRepositoryPort

```java
public interface WarehouseRepositoryPort {

    Warehouse save(Warehouse warehouse);

    void update(Warehouse warehouse);

    Optional<Warehouse> findByIdentifier(Warehouse warehouse);

    boolean existsByIdentifier(Warehouse warehouse);

    List<Warehouse> findAll();
}
```
