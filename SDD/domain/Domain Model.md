# Domain Model — NexusMarket

## Introducción

El Modelo de Dominio representa las entidades centrales del marketplace **NexusMarket** definidas en la *Especificación Funcional del Negocio* (`SDD/enunciado.md`). Sigue Diseño Orientado a Objetos y DDD: la herencia se usa solo para especializaciones reales y las relaciones entre objetos se expresan como referencias explícitas en lugar de identificadores sueltos.

Todo el código Java está en inglés (paquete `application.domain.models`); esta documentación está en español. **Cada tabla de este documento corresponde 1:1 con los atributos de la clase Java** del mismo nombre.

Los campos acotados (estados, roles, tipos) **nunca** son `String` abiertos: son Value Objects de catálogo (ver `Domain Value Objects.md`).

---

# Jerarquía de clases

```text
Person (Abstract)
├── User
├── Buyer
└── Seller

MarketplaceAsset (Abstract)
├── Warehouse
├── Product ──────────── ProductVariant (1..*)
├── InventoryItem
├── Order ────────────── OrderLine (1..*)
├── Invoice
├── Shipment
└── ReturnRequest ────── Refund (0..1)

Cart ─────────────────── CartItem (0..*)
InventoryMovement
Operation
AuditLog
AuthenticationResult
AdministrativeReport
```

# Relaciones

```text
User ── buyer ──> Buyer            (rol BUYER)
User ── seller ─> Seller           (rol SELLER)
Seller ── owns ──> Warehouse (SELLER)     Warehouse (MARKETPLACE) sin propietario
Seller ── publishes ──> Product ── has ──> ProductVariant
InventoryItem ── warehouse ──> Warehouse ;  ── product/variant ──> Product/ProductVariant
InventoryMovement ── inventoryItem ──> InventoryItem ; ── order ──> Order
Buyer ── has ──> Cart ── items ──> CartItem ──> ProductVariant
Order ── buyer ──> Buyer ; ── lines ──> OrderLine ── warehouse ──> Warehouse
Invoice ── order ──> Order (1:1)      Shipment ── order ──> Order (1:1)
ReturnRequest ── order ──> Order ; ── refund ──> Refund
MarketplaceAsset ── generates ──> Operation ── recorded in ──> AuditLog
```

---

# Person

**Tipo:** Abstracta

Persona identificable del marketplace. Centraliza identidad y datos de contacto compartidos por usuarios, compradores y vendedores, y sus validaciones.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| identification | String | Documento de identidad (personas) o NIT (vendedores). Único en la plataforma. Máx. 20. |
| name | String | Nombre completo o razón social. Obligatorio. Máx. 100. |
| email | String | Correo principal de acceso y comunicación. Debe contener `@` y dominio. Único. |
| phoneNumber | String | Teléfono de contacto: entre 7 y 15 dígitos. |
| address | String | Dirección principal (para el comprador, dirección habitual de entrega). Máx. 200. |

## Comportamiento y reglas

* `validateContactData()`: identificación, nombre, correo (con `@` y dominio), teléfono (7–15 dígitos) y dirección obligatorios.

---

# User

**Tipo:** Hereda de `Person`

Identidad del sistema para autenticación y autorización (Dominio 1). Cada usuario tiene **un único rol** (RG-02).

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| userId | Integer | Identificador interno inmutable del usuario (claim `sub` del JWT). |
| username | String | Nombre de acceso. Único. 4 a 50 caracteres. |
| password | String | Contraseña almacenada cifrada (BCrypt). Mínimo 8 caracteres en texto plano. |
| role | UserRole | Rol único del usuario (RG-02). |
| status | UserStatus | Condición operativa: Activo o Bloqueado. |
| buyer | Buyer | Perfil de comprador (solo si el rol es BUYER). |
| seller | Seller | Perfil de vendedor (solo si el rol es SELLER). |
| authTokenVersion | Integer | Versión de token (claim `ver`). Se incrementa en logout, cambio de contraseña o de estado, invalidando tokens anteriores. |

## Comportamiento y reglas

* `activateOnRegistration()`: valida datos y consistencia rol/perfil (BUYER→`buyer`, SELLER→`seller`, personal→sin perfil) y deja el usuario ACTIVE.
* `changeStatus(UserStatus)`: ACTIVE ↔ BLOCKED; incrementa `authTokenVersion`.
* `changePassword(String)`, `bumpAuthTokenVersion()`, `syncContactData(Person)`, `isStaff()`, `hasAnyRole(...)`.

---

# Buyer

**Tipo:** Hereda de `Person`

Comprador (Dominio 2). Nunca administra información de otros compradores ni inventarios.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| additionalAddresses | List\<String\> | Direcciones adicionales de entrega (máximo 5). |
| status | BuyerStatus | Estado comercial: condición del comprador para realizar compras. |

## Comportamiento y reglas

* `register()`: estado comercial inicial ACTIVE.
* `isRegisteredAddress(String)`: la entrega solo puede ir a la dirección principal o a una adicional.
* `changeStatus(BuyerStatus)`: ACTIVE ↔ SUSPENDED (solo ADMIN).
* `updateProfile(Buyer)`: contacto y direcciones adicionales; la identificación no cambia.

---

# Seller

**Tipo:** Hereda de `Person`

Vendedor (Dominio 3). **No puede auto-registrarse**: lo incorpora el Administrador junto con su primera bodega.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| storeName | String | Nombre comercial de la tienda. Obligatorio. |
| status | SellerStatus | Estado comercial del vendedor. |
| registrationDate | LocalDate | Fecha de incorporación por el Administrador. |

## Comportamiento y reglas

* `register()`: exige `storeName`; estado ACTIVE y fecha de registro.
* `changeStatus(SellerStatus)`: ACTIVE ↔ SUSPENDED.
* Un vendedor suspendido no publica productos y sus productos no se pueden agregar al carrito.

---

# MarketplaceAsset

**Tipo:** Abstracta

Activo de negocio afectado por operaciones y referenciado por la bitácora (equivalente a `BankingProduct` del proyecto base).

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| identifier | String | Identificador de negocio único (p. ej. `WH-MKT-01`, `PRD-…`, `ORD-…`). |

---

# Warehouse

**Tipo:** Hereda de `MarketplaceAsset`

Espacio físico de almacenamiento (Dominio 4). Se distinguen bodegas del Marketplace y bodegas de Vendedores.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| name | String | Nombre de la bodega. |
| city | String | Ciudad. |
| address | String | Dirección física. |
| warehouseType | WarehouseType | Clasificación: bodega del Marketplace o bodega de Vendedor. |
| owner | Seller | Vendedor propietario (obligatorio solo para bodegas SELLER). |
| status | WarehouseStatus | Estado operativo de la bodega. |

## Comportamiento y reglas

* `register()`: código `[A-Z0-9-]{3,20}`, tipo obligatorio; SELLER exige propietario y MARKETPLACE no lo admite.
* `canStore(Product)`: MARKETPLACE almacena cualquier producto; SELLER solo productos de su propietario.
* `activate()` / `deactivate()` / `requireActive()`.

---

# Product

**Tipo:** Hereda de `MarketplaceAsset`

Producto del catálogo (Dominio 5). Raíz de agregado de sus variantes.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| seller | Seller | Vendedor que registra y administra el producto. |
| name | String | Nombre del producto (máx. 150). |
| description | String | Descripción comercial. |
| category | ProductCategory | Categoría del catálogo. |
| productType | ProductType | Físico (requiere inventario y despacho) o Digital (entrega inmediata tras el pago). |
| status | ProductStatus | Publicado, Suspendido o Descontinuado. |
| variants | List\<ProductVariant\> | Variantes (color, talla, modelo, etc.). Al menos una. |
| createdAt | LocalDateTime | Fecha de creación/publicación. |

## Comportamiento y reglas

* `publish()`: valida y publica (estado inicial PUBLISHED, visible en el catálogo público).
* `addVariant(...)`, `changeVariantPrice(...)`: SKU único y misma moneda.
* `suspend()` PUBLISHED→SUSPENDED, `reactivate()` SUSPENDED→PUBLISHED, `discontinue()` →DISCONTINUED (final).
* `isPurchasable()`, `isPhysical()`, `isOwnedBy(Seller)`.

---

# ProductVariant

**Tipo:** Entidad del agregado `Product`

Variante comprable (color, talla, modelo…), identificada globalmente por su SKU.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| sku | String | Código único de la variante en todo el marketplace. |
| description | String | Diferencia de la variante (p. ej. `16 GB RAM`). |
| price | Money | Precio (IVA incluido) como Value Object `Money`. |

## Comportamiento y reglas

* `validate()`: SKU `[A-Z0-9-]{3,40}`, descripción y precio > 0.

---

# InventoryItem

**Tipo:** Hereda de `MarketplaceAsset`

Existencia de una variante en una bodega (Dominio 6, inventario distribuido). Siempre vinculada a un producto y a una bodega.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| warehouse | Warehouse | Bodega donde está la existencia (vínculo obligatorio). |
| product | Product | Producto al que pertenece la existencia (vínculo obligatorio). |
| variant | ProductVariant | Variante específica. |
| availableQuantity | int | Unidades disponibles para reservar. Nunca negativas. |
| reservedQuantity | int | Unidades reservadas por pedidos pendientes de pago. |
| damagedQuantity | int | Unidades marcadas como dañadas: no se pueden reservar ni vender. |

## Comportamiento y reglas

* `open(...)`: solo productos físicos y en bodegas que puedan almacenarlos.
* `registerInflow(n)` (INFLOW), `reserve(n)` (RESERVATION, solo sobre disponible: nunca sobre dañado), `releaseReservation(n)`, `confirmSale(n)` (SALE), `adjustAvailableTo(n)` y `markDamaged(n)` (ADJUSTMENT), `registerReturn(n, damaged)` (RETURN).
* **Invariante:** disponible, reservado y dañado nunca son negativos (`NegativeStockException` / `InsufficientStockException`).

---

# InventoryMovement

**Tipo:** Entidad

Registro inmutable de cada cambio de inventario.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| movementId | Long | Identificador del movimiento. |
| inventoryItem | InventoryItem | Existencia afectada. |
| movementType | MovementType | Ingreso, Reserva, Liberación de reserva, Salida por venta, Ajuste o Devolución. |
| quantity | int | Unidades (positivo; en Ajuste es la diferencia con signo). |
| occurredAt | LocalDateTime | Fecha y hora del movimiento. |
| performedBy | User | Usuario que lo ejecutó (nulo en procesos del sistema). |
| order | Order | Pedido relacionado (reserva, venta, liberación o devolución). |
| note | String | Observación (motivo de ajuste, devolución, etc.). |

## Comportamiento y reglas

* `record(...)`: fábrica con validaciones de tipo y cantidad.

---

# Cart

**Tipo:** Entidad

Carrito de compras (OBJ-07): selección provisional de productos.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| cartId | String | Identificador del carrito (`CART-{identificación del comprador}`). |
| buyer | Buyer | Comprador dueño del carrito (uno activo por comprador). |
| items | List\<CartItem\> | Productos seleccionados provisionalmente. |
| updatedAt | LocalDateTime | Última modificación. |

## Comportamiento y reglas

* `addItem(product, sku, qty)`: producto publicado, vendedor activo, cantidad > 0, misma moneda; si el SKU ya está, suma cantidades.
* `updateQuantity`, `removeItem`, `clear`, `getTotal()`.

---

# CartItem

**Tipo:** Entidad del agregado `Cart`

Línea del carrito.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| product | Product | Producto seleccionado. |
| variant | ProductVariant | Variante seleccionada. |
| quantity | int | Cantidad (> 0; productos digitales: 1). |
| unitPrice | Money | Precio unitario capturado al agregar. |

## Comportamiento y reglas

* `getSubtotal()`.

---

# Order

**Tipo:** Hereda de `MarketplaceAsset`

Pedido: compromiso comercial formal y proceso central (Dominio 7).

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| buyer | Buyer | Comprador que realiza el pedido. |
| lines | List\<OrderLine\> | Líneas del pedido (copia inmutable del carrito). |
| status | OrderStatus | Estado del ciclo de vida del pedido. |
| totalAmount | Money | Total a pagar (IVA incluido). |
| shippingAddress | String | Dirección de entrega: principal o adicional del comprador (solo pedidos con productos físicos). |
| paymentMethod | PaymentMethod | Medio de pago registrado. |
| createdAt | LocalDateTime | Fecha de creación. |
| paidAt | LocalDateTime | Fecha de pago. |
| shippedAt | LocalDateTime | Fecha de despacho. |
| deliveredAt | LocalDateTime | Fecha de entrega (finalización). |
| cancelledAt | LocalDateTime | Fecha de cancelación / vencimiento. |

## Comportamiento y reglas

* `draftFrom(cart, address)`: crea el pedido en **CART** congelando precios; valida comprador habilitado y dirección registrada.
* `allocateLine(n, warehouse)`, `submitForPayment()` CART→PENDING_PAYMENT (todas las líneas físicas con reserva).
* `markPaid(method)` PENDING_PAYMENT→PAID, `markShipped()` PAID→SHIPPED, `markDelivered()` SHIPPED→DELIVERED (o PAID→DELIVERED si es solo digital).
* `cancel()` CART/PENDING_PAYMENT→CANCELLED (cancelación del comprador o vencimiento del pago).
* **Un pedido finalizado (DELIVERED o CANCELLED) no puede modificarse** (`OrderImmutableException`).

---

# OrderLine

**Tipo:** Entidad del agregado `Order`

Línea del pedido con snapshot de producto, variante, vendedor, precio y bodega asignada.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| lineNumber | int | Consecutivo de la línea. |
| product | Product | Producto (snapshot de id, nombre y tipo). |
| variant | ProductVariant | Variante (SKU). |
| seller | Seller | Vendedor del producto. |
| quantity | int | Cantidad comprada. |
| unitPrice | Money | Precio unitario congelado al hacer checkout. |
| warehouse | Warehouse | Bodega desde la que se reservó la existencia (solo físicos). |

## Comportamiento y reglas

* `getLineTotal()`, `isPhysical()`.

---

# Invoice

**Tipo:** Hereda de `MarketplaceAsset`

Factura de la compra (OBJ-09). Precios con IVA incluido.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| order | Order | Pedido facturado (una factura por pedido). |
| buyer | Buyer | Comprador facturado. |
| issueDate | LocalDateTime | Fecha de emisión. |
| subtotal | Money | Base gravable (total / (1 + IVA)). |
| taxAmount | Money | Valor del IVA. |
| total | Money | Total facturado (= total del pedido). |
| refundedAmount | Money | Acumulado reembolsado; nunca supera el total. |
| status | InvoiceStatus | Emitida, Parcialmente reembolsada o Reembolsada. |

## Comportamiento y reglas

* `issueFor(order, taxRate)`: solo pedidos PAID; subtotal = total/(1+IVA), IVA = total − subtotal.
* `registerRefund(Money)`: acumula reembolsos sin superar el total; actualiza el estado.

---

# Shipment

**Tipo:** Hereda de `MarketplaceAsset`

Envío de un pedido con productos físicos (OBJ-10).

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| order | Order | Pedido despachado (un envío por pedido). |
| logisticOperator | User | Operador logístico responsable. |
| carrier | String | Transportadora. |
| trackingNumber | String | Número de guía. |
| destinationAddress | String | Dirección de entrega (copiada del pedido). |
| status | ShipmentStatus | En tránsito o Entregado. |
| dispatchedAt | LocalDateTime | Fecha de despacho. |
| deliveredAt | LocalDateTime | Fecha de entrega confirmada. |

## Comportamiento y reglas

* `dispatch(order, operator, carrier, tracking)`: solo pedidos PAID con productos físicos; estado IN_TRANSIT.
* `confirmDelivery()`: IN_TRANSIT→DELIVERED.

---

# ReturnRequest

**Tipo:** Hereda de `MarketplaceAsset`

Devolución de unidades de una línea de un pedido entregado (OBJ-11). El pedido original no se modifica.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| order | Order | Pedido entregado del que se devuelven unidades. |
| buyer | Buyer | Comprador solicitante. |
| sku | String | Variante devuelta. |
| quantity | int | Unidades devueltas (no supera lo comprado menos lo ya devuelto). |
| reason | ReturnReason | Motivo de devolución. |
| comment | String | Comentario del comprador. |
| status | ReturnStatus | Estado de la devolución. |
| requestedAt | LocalDateTime | Fecha de solicitud. |
| reviewedBy | User | Administrador que aprobó o rechazó. |
| reviewedAt | LocalDateTime | Fecha de revisión. |
| rejectionReason | String | Motivo del rechazo. |
| receivedBy | User | Operador logístico que recibió el producto. |
| receivedAt | LocalDateTime | Fecha de recepción en bodega. |
| refund | Refund | Reembolso emitido. |

## Comportamiento y reglas

* `request(...)`: pedido DELIVERED y propio, producto físico, cantidad válida, dentro de la ventana de días configurada.
* `approve(admin)` / `reject(admin, motivo)`: REQUESTED→APPROVED/REJECTED.
* `markReceived(operator)`: APPROVED→RECEIVED.
* `issueRefund(admin)`: RECEIVED→REFUNDED, crea el `Refund`.

---

# Refund

**Tipo:** Entidad

Reembolso al comprador, emitido por el Administrador tras recibir el producto.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| identifier | String | Identificador del reembolso (`REF-{devolución}`). |
| amount | Money | Valor reembolsado = precio unitario × unidades devueltas. |
| issuedAt | LocalDateTime | Fecha de emisión. |
| issuedBy | User | Administrador que emitió el reembolso. |

---

# Operation

**Tipo:** Entidad

Operación de negocio registrada en la base relacional.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| operationId | Long | Identificador de la operación (SQL). |
| operationType | OperationType | Tipo de operación de negocio. |
| executionDate | LocalDateTime | Fecha y hora. |
| performedBy | User | Usuario que la ejecutó (nulo en procesos del sistema). |
| affectedAsset | MarketplaceAsset | Activo afectado (bodega, producto, existencia, pedido, factura, envío o devolución). |

---

# AuditLog

**Tipo:** Entidad

Registro inmutable de la bitácora (MongoDB) con un documento `details` variable.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| auditId | String | Identificador del documento de auditoría (MongoDB). |
| operationType | OperationType | Tipo de operación. |
| operationDate | LocalDateTime | Fecha y hora. |
| performedBy | User | Usuario que ejecutó la acción. |
| userRole | UserRole | Rol del usuario al momento de la operación. |
| affectedAsset | MarketplaceAsset | Activo afectado. |
| details | Map\<String, Object\> | Documento variable con los datos propios de cada operación (antes/después, montos, motivos). |

---

# AuthenticationResult

**Tipo:** Objeto de resultado

Resultado del login.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| authenticatedUser | User | Usuario autenticado. |
| token | String | JWT emitido. |

---

# AdministrativeReport

**Tipo:** Modelo de lectura

Información administrativa consolidada para consulta (OBJ-12). Se calcula bajo demanda, no se persiste.

## Atributos

| Atributo | Tipo | Descripción |
| --- | --- | --- |
| generatedAt | LocalDateTime | Momento de generación. |
| usersByRole | Map\<String, Long\> | Cantidad de usuarios por rol. |
| ordersByStatus | Map\<String, Long\> | Cantidad de pedidos por estado. |
| productsByStatus | Map\<String, Long\> | Cantidad de productos por estado. |
| returnsByStatus | Map\<String, Long\> | Cantidad de devoluciones por estado. |
| totalInvoiced | Money | Total facturado. |
| totalRefunded | Money | Total reembolsado. |
| lowStockItems | List\<InventoryItem\> | Existencias con disponible menor o igual al umbral configurado. |

---

# Reglas transversales del dominio

| Código | Regla | Dónde se garantiza |
| --- | --- | --- |
| RG-01 | Toda operación la ejecuta un usuario autenticado. | `JwtAuthenticationFilter` + `ValidateUserAuthorizationService.requireActive` |
| RG-02 | Cada usuario tiene un único rol. | `User.role` (un solo valor) + `User.activateOnRegistration` |
| RG-03 | Ningún participante administra información fuera de su rol. | Puertos de entrada por rol + `ValidateOwnershipService` / `InventoryAccessService` |
| V-INV | No se reserva inventario inexistente o marcado como dañado; no hay existencias negativas. | `InventoryItem.reserve`, `markDamaged`, `adjustAvailableTo` |
| V-PED | Un pedido finalizado no puede modificarse. | `Order.ensureMutable` → `OrderImmutableException` |
| V-USR | Documento de identidad y correo únicos. | `UserRegistrationRulesService.ensureUnique`, restricciones `unique` en MySQL |
| R-VEN | Los vendedores no se auto-registran; los incorpora el Administrador con su primera bodega. | `RegisterSellerService` (solo ADMIN) |
| R-DIG | Productos digitales: sin inventario ni despacho; entrega inmediata tras el pago. | `InventoryItem.open`, `PayOrderService` |
