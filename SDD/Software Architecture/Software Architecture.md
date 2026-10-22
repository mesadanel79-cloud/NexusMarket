# Software Architecture — NexusMarket

## 1. Estilo arquitectónico
**Arquitectura Hexagonal (Ports & Adapters) + DDD**, idéntica en estructura al proyecto base del curso (`bank`):

```text
              ┌──────────────────────── adapters (entrada) ────────────────────────┐
  HTTP/JSON → │ rest/controllers → rest/mappers → useCases/*UseCaseImpl           │
              └───────────────────────────────┬────────────────────────────────────┘
                                              │ domain/ports/in (1 puerto por rol)
              ┌───────────────────────────────▼────────────────────────────────────┐
              │ DOMAIN (Java puro): models · valueobjects · enums · exceptions      │
              │                     services (lógica de negocio)                    │
              └───────────────────────────────┬────────────────────────────────────┘
                                              │ domain/ports/out
              ┌───────────────────────────────▼──────────── adapters (salida) ─────┐
              │ persistence/jpa (MySQL) · persistence/mongodb (auditoría)           │
              │ security (JWT, BCrypt) · config (parámetros de negocio)             │
              └─────────────────────────────────────────────────────────────────────┘
  infrastructure: DomainServicesConfig (registro de servicios), seed, scheduling
```

## 2. Reglas de dependencia
1. `domain` no importa Spring, JPA, MongoDB, Jackson, JJWT ni Lombok (verificable con `grep`). Los servicios de dominio no tienen anotaciones: `infrastructure/config/DomainServicesConfig` los registra con un `@ComponentScan` por expresión regular (`.*Service$`).
2. Los adaptadores dependen del dominio, nunca al revés.
3. Los controladores solo conocen los **puertos de entrada** (interfaces por rol) y los mappers REST.
4. Cada adaptador de persistencia tiene sus propias **Entities/Documents**, **Mappers** y **Repositories**.

## 3. Estructura de paquetes
```text
nexusmarket/src/main/java/application/
├── NexusMarketApplication.java
├── domain/
│   ├── models/          Person, User, Buyer, Seller, MarketplaceAsset, Warehouse, Product, ProductVariant,
│   │                    InventoryItem, InventoryMovement, Cart, CartItem, Order, OrderLine, Invoice,
│   │                    Shipment, ReturnRequest, Refund, Operation, AuditLog, AuthenticationResult,
│   │                    AdministrativeReport
│   ├── valueobjects/    DomainCatalog, Money, Currency, UserRole, UserStatus, BuyerStatus, SellerStatus,
│   │                    WarehouseType, WarehouseStatus, ProductType, ProductStatus, ProductCategory,
│   │                    MovementType, OrderStatus, PaymentMethod, InvoiceStatus, ShipmentStatus,
│   │                    ReturnStatus, ReturnReason, OperationType
│   ├── enums/           ApprovalDecision
│   ├── exceptions/      DomainException + 25 excepciones de negocio
│   ├── ports/in/        PublicAccessPort, BuyerPort, SellerPort, LogisticOperatorPort, SupervisorPort, AdminPort
│   ├── ports/out/       17 puertos de salida (repositorios, JWT, password, configuración)
│   └── services/        authorization, user, buyer, seller, warehouse, inventory, catalog, cart, order,
│                        invoice, shipment, returns, operation, report
├── adapters/
│   ├── useCases/        *UseCaseImpl (implementan los puertos de entrada; @Transactional)
│   ├── rest/            controllers, dtos (requests/responses como records), mappers, exception
│   ├── persistence/jpa/ entities, mappers, repositories (Spring Data JPA), *JpaAdapter
│   ├── persistence/mongodb/ documents, mappers, repositories, AuditLogMongoAdapter
│   ├── security/        SecurityConfig, JwtProvider, JwtAuthenticationFilter, BCryptPasswordServiceAdapter,
│   │                    dtos/AuthenticatedUserPrincipal, mappers/JwtClaimsMapper
│   └── config/          DefaultBusinessConfigurationAdapter
└── infrastructure/
    ├── config/          DomainServicesConfig
    ├── scheduling/      ExpirePendingOrdersScheduler
    └── seed/            DatabaseSeeder
```

## 4. Flujo de una petición
1. `JwtAuthenticationFilter` valida el token, **reconstruye el `User` desde los claims** y verifica contra la BD que siga ACTIVE y que `ver` coincida con `authTokenVersion`.
2. Spring Security valida que el rol del token corresponda al prefijo de la ruta (`/api/v1/buyer/**` → `BUYER`, …).
3. El controlador convierte el `RequestDTO` a modelo de dominio (mapper) e invoca el **puerto de entrada del rol** inyectando el `User` reconstruido.
4. El caso de uso abre la transacción y llama al servicio de dominio.
5. El servicio valida rol/propiedad, aplica el comportamiento del modelo y usa los puertos de salida.
6. Los adaptadores de persistencia convierten con sus mappers a Entity/Document y usan Spring Data.
7. Se registra la operación (MySQL) y la auditoría (MongoDB).
8. El controlador convierte el resultado a `ResponseDTO`.

## 5. Tecnología
| Capa | Tecnología |
| --- | --- |
| Lenguaje / build | Java 17, Maven (wrapper `mvnw`) |
| Framework | Spring Boot 4.1 (Web MVC, Security, Validation, Actuator) |
| Relacional | Spring Data JPA / Hibernate → MySQL 8 (`nexus_db`, DDL automático) |
| NoSQL | Spring Data MongoDB → `audit_db.audit_logs` |
| Seguridad | JWT HS256 (JJWT 0.12.6), BCrypt, sesiones stateless, CORS |
| Pruebas | JUnit 5 (servicios con dobles en memoria, sin BD) |
| Contenedores | Docker Compose (MySQL 3306, MongoDB 27017, app 8080) |

## 6. Modelo relacional (MySQL, generado por Hibernate)
`users`, `buyers`, `buyer_additional_addresses`, `sellers`, `warehouses`, `products`, `product_variants`, `inventory_items`, `inventory_movements`, `carts`, `cart_items`, `orders`, `order_lines`, `invoices`, `shipments`, `return_requests`, `operations`. Las referencias entre agregados se guardan como identificadores; los catálogos como su código.

## 7. Modelo documental (MongoDB)
Colección `audit_logs`: `{ _id, operationType, operationDate, performedByUserId, performedByUsername, userRole, affectedAssetIdentifier, affectedAssetType, details{...} }`. Append-only.

## 8. Transacciones y consistencia
Los casos de uso son `@Transactional`: el cambio de estado, los movimientos de inventario y la operación SQL se confirman juntos (si falta stock en el checkout, no queda ninguna reserva). La auditoría en MongoDB no participa de la transacción JPA (limitación conocida, igual que en el proyecto base).
