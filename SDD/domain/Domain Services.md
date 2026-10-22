# Domain Services — NexusMarket

## 1. Propósito

Los servicios de dominio (`application.domain.services.*`) contienen la lógica de negocio que coordina varios agregados y los puertos de salida. Son **Java puro** (sin anotaciones de Spring): `infrastructure/config/DomainServicesConfig` los registra como beans por convención de nombre (`*Service`) e inyecta sus dependencias por constructor. Los casos de uso (`adapters/useCases`) los invocan y definen el límite transaccional (`@Transactional`).

Reglas comunes a todos los servicios:

1. **Autorización primero**: antes de cargar datos se valida que el usuario esté ACTIVO y tenga el rol permitido (`ValidateUserAuthorizationService`); luego la propiedad del recurso (`ValidateOwnershipService`, `InventoryAccessService`). Esto implementa RG-01, RG-02 y RG-03.
2. **El modelo de dominio decide las transiciones**: los servicios nunca asignan estados directamente; invocan el comportamiento del modelo (`order.markPaid`, `item.reserve`, …).
3. **Trazabilidad**: toda operación que cambia estado se registra con `RegisterOperationAndAuditService` → `Operation` (MySQL) + `AuditLog` con `details` variable (MongoDB).
4. **Datos autoritativos**: el `User` del JWT solo trae referencias; el servicio carga el comprador/vendedor/pedido real desde los puertos de salida.

## 2. Matriz de responsabilidades (Especificación §12) → servicios

| Proceso | Comprador | Vendedor | Op. Logístico | Admin | Supervisor |
| --- | :---: | :---: | :---: | :---: | :---: |
| Registro de vendedores (`RegisterSellerService`) | | | | ✔ | |
| Registro de productos (`CreateProductService`, `ManageProductVariantsService`, `ChangeProductStatusService`) | | ✔ | | | consulta |
| Administración de inventario (`RegisterInventoryInflowService`, `AdjustInventoryService`) | | ✔ (sus productos) | ✔ | | consulta |
| Gestión de pedidos (`CheckoutService`, `PayOrderService`, `CancelOrderService`, `DispatchOrderService`, `ConfirmDeliveryService`, `ConsultOrdersService`) | ✔ | ✔ (sus ventas) | ✔ (despacho) | | consulta |
| Gestión de reembolsos (`RequestReturnService`, `ReviewReturnService`, `IssueRefundService`) | ✔ (solicita) | | ✔ (recibe) | ✔ (aprueba / reembolsa) | consulta |
| Administración de bodegas (`RegisterWarehouseService`, `ChangeWarehouseStatusService`) | | | consulta | ✔ | consulta |
| Administración de usuarios / compradores (`RegisterStaffUserService`, `ChangeUserStatusService`, `ChangeBuyerStatusService`) | | | | ✔ | |
| Reportes administrativos (`ConsultAdministrativeReportService`, `ConsultOperationsService`) | | | | ✔ | ✔ |

## 3. Catálogo de servicios

| Paquete | Servicio | Responsabilidad | Detalle |
| --- | --- | --- | --- |
| authorization | `ValidateUserAuthorizationService` | Usuario presente, ACTIVE y con rol permitido. | `services/authorization-services.md` |
| authorization | `ValidateOwnershipService` | Resuelve el perfil comprador/vendedor y valida propiedad de pedidos y productos. | idem |
| user | `LoginService`, `LogoutService`, `LoadAuthenticatedUserService` | Autenticación JWT e invalidación por versión de token. | `services/user-services.md` |
| user | `RegisterBuyerService` | Auto-registro público del comprador. | idem |
| user | `RegisterSellerService` | Alta del vendedor + usuario + primera bodega (solo ADMIN). | idem |
| user | `RegisterStaffUserService`, `ChangeUserStatusService`, `ChangePasswordService`, `ConsultUsersService`, `UserRegistrationRulesService` | Administración de usuarios. | idem |
| buyer | `ConsultBuyerProfileService`, `UpdateBuyerProfileService`, `ChangeBuyerStatusService` | Gestión de compradores. | idem |
| seller | `ConsultSellersService`, `ChangeSellerStatusService` | Gestión de vendedores. | idem |
| warehouse | `RegisterWarehouseService`, `ChangeWarehouseStatusService`, `ConsultWarehousesService` | Gestión de bodegas. | `services/warehouse-inventory-services.md` |
| inventory | `RegisterInventoryInflowService`, `AdjustInventoryService`, `ConsultInventoryService`, `InventoryAccessService`, `InventoryAllocationService` | Inventario distribuido. | idem |
| catalog | `CreateProductService`, `ManageProductVariantsService`, `ChangeProductStatusService`, `ConsultCatalogService` | Catálogo. | `services/catalog-services.md` |
| cart | `ConsultCartService`, `ManageCartService` | Carrito. | `services/cart-order-services.md` |
| order | `CheckoutService`, `PayOrderService`, `CancelOrderService`, `ExpirePendingOrdersService`, `ConsultOrdersService` | Ciclo del pedido. | idem |
| invoice | `IssueInvoiceService`, `ConsultInvoicesService` | Facturación. | `services/invoice-shipment-services.md` |
| shipment | `DispatchOrderService`, `ConfirmDeliveryService`, `ConsultShipmentsService` | Logística. | idem |
| returns | `RequestReturnService`, `ReviewReturnService`, `ReceiveReturnService`, `IssueRefundService`, `ConsultReturnsService` | Devoluciones y reembolsos. | `services/return-refund-services.md` |
| operation | `RegisterOperationService`, `RegisterAuditLogService`, `RegisterOperationAndAuditService`, `ConsultOperationsService` | Bitácora (SQL + NoSQL). | `services/operation-audit-report-services.md` |
| report | `ConsultAdministrativeReportService` | Información administrativa consolidada (OBJ-12). | idem |

## 4. Catálogo de excepciones de dominio (`application.domain.exceptions`)

Todas extienden `DomainException` (RuntimeException). Mapeo HTTP en `SDD/Adapters/Global-exception-handler.md`.

| Excepción | Cuándo | HTTP |
| --- | --- | --- |
| `InvalidCredentialsException` | Usuario o contraseña incorrectos. | 401 |
| `UnauthorizedOperationException` | Usuario inactivo/bloqueado, rol no permitido o recurso ajeno. | 403 |
| `EntityNotFoundException` | Recurso inexistente. | 404 |
| `InvalidCatalogValueException` | Código de catálogo desconocido. | 400 |
| `UserAlreadyExistsException` | Usuario, identificación o correo repetidos. | 409 |
| `InvalidStatusTransitionException` | Transición de estado no permitida. | 409 |
| `OrderImmutableException` | Intento de modificar un pedido finalizado. | 409 |
| `InsufficientStockException` | No hay unidades disponibles (no dañadas) suficientes. | 409 |
| `NegativeStockException` | La operación dejaría existencias negativas. | 409 |
| `InvalidPersonDataException`, `InvalidUserException`, `InvalidBuyerException`, `InvalidSellerException`, `InvalidWarehouseException`, `InvalidProductException`, `InvalidInventoryOperationException`, `InvalidCartException`, `InvalidOrderException`, `InvalidInvoiceException`, `InvalidShipmentException`, `InvalidReturnException`, `InvalidOperationException`, `InvalidAuditLogException`, `InvalidMoneyException`, `CurrencyMismatchException` | Violación de invariantes de cada agregado. | 409 |
