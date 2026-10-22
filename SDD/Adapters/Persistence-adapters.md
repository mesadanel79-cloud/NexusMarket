# Persistence Adapters

## Relacional — `adapters/persistence/jpa` (MySQL `nexus_db`)
Cada adaptador implementa un puerto de salida y usa **su propia Entity (repository DTO), Mapper y Repository** de Spring Data JPA. El dominio nunca ve una `@Entity`.

| Puerto | Adaptador | Entities | Repository |
| --- | --- | --- | --- |
| UserRepositoryPort | UserJpaAdapter | UserJpaEntity (`users`) | SpringDataUserRepository |
| BuyerRepositoryPort | BuyerJpaAdapter | BuyerJpaEntity (`buyers` + `buyer_additional_addresses`) | SpringDataBuyerRepository |
| SellerRepositoryPort | SellerJpaAdapter | SellerJpaEntity (`sellers`) | SpringDataSellerRepository |
| WarehouseRepositoryPort | WarehouseJpaAdapter | WarehouseJpaEntity (`warehouses`) | SpringDataWarehouseRepository |
| ProductRepositoryPort | ProductJpaAdapter | ProductJpaEntity + ProductVariantJpaEntity | SpringDataProductRepository, SpringDataProductVariantRepository |
| InventoryRepositoryPort + InventoryMovementRepositoryPort | InventoryJpaAdapter | InventoryItemJpaEntity, InventoryMovementJpaEntity | SpringDataInventoryItemRepository, SpringDataInventoryMovementRepository |
| CartRepositoryPort | CartJpaAdapter | CartJpaEntity + CartItemJpaEntity | SpringDataCartRepository |
| OrderRepositoryPort | OrderJpaAdapter | OrderJpaEntity + OrderLineJpaEntity | SpringDataOrderRepository |
| InvoiceRepositoryPort | InvoiceJpaAdapter | InvoiceJpaEntity | SpringDataInvoiceRepository |
| ShipmentRepositoryPort | ShipmentJpaAdapter | ShipmentJpaEntity | SpringDataShipmentRepository |
| ReturnRequestRepositoryPort | ReturnRequestJpaAdapter | ReturnRequestJpaEntity (incluye columnas del reembolso) | SpringDataReturnRequestRepository |
| OperationRepositoryPort | OperationJpaAdapter | OperationJpaEntity (`operations`) | SpringDataOperationRepository |

Convenciones:
* Value Objects de catálogo → columna con su **código** (`JpaMapperSupport.code`); `Money` → `amount` + `currency`.
* Referencias entre agregados → identificador (p. ej. `seller_identification`, `warehouse_identifier`).
* Hijos de un agregado (variantes, ítems del carrito, líneas del pedido) → `@OneToMany(cascade = ALL, orphanRemoval = true)` con id natural (`ORD-…-1`, `CART-…-SKU`).
* Las líneas del pedido guardan un **snapshot** (nombre y tipo de producto, SKU, vendedor, precio, bodega) para que el pedido no cambie si cambia el catálogo.
* Identificadores generados por el adaptador si llegan vacíos: `PRD-`, `ORD-`, `INVC-`, `SHP-`, `RET-` + 12 caracteres.

## NoSQL — `adapters/persistence/mongodb` (MongoDB `audit_db`)
| Puerto | Adaptador | Document | Repository |
| --- | --- | --- | --- |
| AuditLogRepositoryPort | AuditLogMongoAdapter | AuditLogDocument (`audit_logs`) | AuditLogMongoRepository |

El usuario y el activo afectado se guardan desnormalizados (id, username, rol, tipo e identificador del activo) junto al documento `details`.
