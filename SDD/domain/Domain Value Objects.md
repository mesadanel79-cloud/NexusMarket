# Domain Value Objects — NexusMarket

Los valores acotados del negocio **no se modelan como `String` abiertos**. Se usan Value Objects de catálogo inmutables que extienden `DomainCatalog` (patrón del proyecto base): cada valor es un singleton con `code`, `name` y `description`, se compara por clase + código y se resuelve con `fromCode(String)` (lanza `InvalidCatalogValueException` si el código no existe).

Adicionalmente:

* `Money` (amount `BigDecimal` con 2 decimales + `Currency`): nunca negativo, no mezcla monedas (`CurrencyMismatchException`).
* `ApprovalDecision` (enum Java en `domain/enums`): `APPROVED`, `REJECTED` para la revisión de devoluciones.

Ubicación: `application.domain.valueobjects`.

---

## UserRole

Rol único del usuario (RG-02).

| Código | Nombre | Descripción |
| --- | --- | --- |
| `BUYER` | Buyer | Customer that buys products in the marketplace. |
| `SELLER` | Seller | Merchant that publishes and sells products. Registered only by an administrator. |
| `LOGISTIC_OPERATOR` | Logistic Operator | Runs the physical operation of warehouses: stock, packing, dispatch and returned items. |
| `ADMIN` | Administrator | Administers users, buyers, sellers and warehouses; approves returns and issues refunds. |
| `SUPERVISOR` | Supervisor | Read-only profile for operational follow-up, reports and audit. |

## UserStatus

Condición operativa del usuario.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `ACTIVE` | Active | User can access the platform. |
| `BLOCKED` | Blocked | User access has been suspended. |

## BuyerStatus

Estado comercial del comprador.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `ACTIVE` | Active | Buyer can place orders. |
| `SUSPENDED` | Suspended | Buyer is temporarily not allowed to place orders. |

## SellerStatus

Estado comercial del vendedor.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `ACTIVE` | Active | Seller can publish and sell products. |
| `SUSPENDED` | Suspended | Seller cannot publish products nor receive new sales. |

## WarehouseType

Clasificación de bodegas (Marketplace / Vendedor).

| Código | Nombre | Descripción |
| --- | --- | --- |
| `MARKETPLACE` | Marketplace Warehouse | Warehouse operated by NexusMarket; can store products of any seller. |
| `SELLER` | Seller Warehouse | Warehouse owned by a seller; stores only that seller's products. |

## WarehouseStatus

Estado operativo de la bodega.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `ACTIVE` | Active | Warehouse can receive and dispatch stock. |
| `INACTIVE` | Inactive | Warehouse cannot receive nor dispatch stock. |

## ProductType

Tipo de producto (Físico / Digital).

| Código | Nombre | Descripción |
| --- | --- | --- |
| `PHYSICAL` | Physical | Tangible product that requires warehouse stock and shipping. |
| `DIGITAL` | Digital | Downloadable or licensed product without stock nor shipping. |

## ProductStatus

Estado del producto (Publicado / Suspendido / Descontinuado).

| Código | Nombre | Descripción |
| --- | --- | --- |
| `PUBLISHED` | Published | Product is visible and can be purchased. |
| `SUSPENDED` | Suspended | Product is temporarily hidden and cannot be purchased. |
| `DISCONTINUED` | Discontinued | Product has been permanently withdrawn from the catalog. |

## ProductCategory

Categoría del catálogo.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `ELECTRONICS` | Electronics | Electronic devices and accessories. |
| `FASHION` | Fashion | Clothing, shoes and accessories. |
| `HOME` | Home | Home and kitchen products. |
| `BOOKS` | Books | Printed and digital books. |
| `SPORTS` | Sports | Sports and outdoor products. |
| `SOFTWARE` | Software | Software licenses and digital tools. |
| `OTHER` | Other | Products without a specific category. |

## MovementType

Tipo de movimiento de inventario (Ingreso, Reserva, Salida por venta, Ajuste, Devolución + Liberación de reserva).

| Código | Nombre | Descripción |
| --- | --- | --- |
| `INFLOW` | Inflow | Stock received into a warehouse. |
| `RESERVATION` | Reservation | Stock reserved for an order pending payment. |
| `RESERVATION_RELEASE` | Reservation Release | Reserved stock returned to available after an order is cancelled or expires. |
| `SALE` | Sale | Reserved stock consumed by a paid order. |
| `ADJUSTMENT` | Adjustment | Manual correction after a physical count or units marked as damaged. |
| `RETURN` | Return | Returned item received back into a warehouse (as available or damaged stock). |

## OrderStatus

Ciclo de estados del pedido.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `CART` | Cart | Provisional selection of products taken from the buyer's cart; stock not reserved yet. |
| `PENDING_PAYMENT` | Pending Payment | Stock reserved; waiting for payment. |
| `PAID` | Paid | Payment confirmed and invoice issued; picking and packing starts. |
| `SHIPPED` | Shipped | Order physically left the warehouse. |
| `DELIVERED` | Delivered | Order delivered. Final and immutable. |
| `CANCELLED` | Cancelled | Order cancelled before payment. Final and immutable. |

## PaymentMethod

Medio de pago.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `CREDIT_CARD` | Credit Card | Payment with credit card. |
| `DEBIT_CARD` | Debit Card | Payment with debit card. |
| `BANK_TRANSFER` | Bank Transfer | Payment through bank transfer (e.g. PSE). |
| `WALLET` | Wallet | Payment with a digital wallet. |

## InvoiceStatus

Estado de la factura.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `ISSUED` | Issued | Invoice issued for a paid order. |
| `PARTIALLY_REFUNDED` | Partially Refunded | Part of the invoice total has been refunded. |
| `REFUNDED` | Refunded | The invoice total has been fully refunded. |

## ShipmentStatus

Estado del envío.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `IN_TRANSIT` | In Transit | Shipment dispatched and travelling to the buyer. |
| `DELIVERED` | Delivered | Shipment delivered to the buyer. |

## ReturnStatus

Estado de la devolución.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `REQUESTED` | Requested | Return requested by the buyer. |
| `APPROVED` | Approved | Return approved by an administrator. |
| `REJECTED` | Rejected | Return rejected by an administrator. Final. |
| `RECEIVED` | Received | Returned item received in the warehouse. |
| `REFUNDED` | Refunded | Refund issued to the buyer. Final. |

## ReturnReason

Motivo de la devolución.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `DEFECTIVE` | Defective | Product arrived damaged or does not work. |
| `WRONG_ITEM` | Wrong Item | A different product was delivered. |
| `NOT_AS_DESCRIBED` | Not As Described | Product does not match its description. |
| `NO_LONGER_NEEDED` | No Longer Needed | Buyer changed their mind. |

## OperationType

Tipo de operación registrada en la bitácora.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `USER_REGISTRATION` | User Registration | Registration of a system user. |
| `USER_STATUS_CHANGE` | User Status Change | User blocked or unblocked. |
| `USER_PASSWORD_CHANGE` | User Password Change | User password changed. |
| `BUYER_REGISTRATION` | Buyer Registration | Registration of a buyer. |
| `BUYER_UPDATE` | Buyer Update | Update of buyer contact data. |
| `BUYER_STATUS_CHANGE` | Buyer Status Change | Change of the buyer commercial status. |
| `SELLER_REGISTRATION` | Seller Registration | Registration of a seller by an administrator. |
| `SELLER_STATUS_CHANGE` | Seller Status Change | Change of the seller commercial status. |
| `WAREHOUSE_REGISTRATION` | Warehouse Registration | Registration of a warehouse. |
| `WAREHOUSE_STATUS_CHANGE` | Warehouse Status Change | Warehouse activated or deactivated. |
| `PRODUCT_CREATION` | Product Creation | Creation and publication of a product. |
| `PRODUCT_VARIANT_ADDITION` | Product Variant Addition | New variant added to a product. |
| `PRODUCT_PRICE_CHANGE` | Product Price Change | Price of a product variant changed. |
| `PRODUCT_SUSPENSION` | Product Suspension | Product suspended by its seller. |
| `PRODUCT_REACTIVATION` | Product Reactivation | Product published again. |
| `PRODUCT_DISCONTINUATION` | Product Discontinuation | Product permanently discontinued. |
| `INVENTORY_INFLOW` | Inventory Inflow | Stock received in a warehouse. |
| `INVENTORY_ADJUSTMENT` | Inventory Adjustment | Manual stock adjustment. |
| `ORDER_CHECKOUT` | Order Checkout | Order created from the cart and stock reserved. |
| `ORDER_PAYMENT` | Order Payment | Order paid, stock sold and invoice issued. |
| `ORDER_CANCELLATION` | Order Cancellation | Order cancelled by the buyer. |
| `ORDER_EXPIRATION` | Order Expiration | Order cancelled automatically because payment did not arrive in time. |
| `ORDER_DELIVERY` | Order Delivery | Order delivered. |
| `INVOICE_ISSUANCE` | Invoice Issuance | Invoice issued for a paid order. |
| `SHIPMENT_DISPATCH` | Shipment Dispatch | Order dispatched to the carrier. |
| `SHIPMENT_DELIVERY` | Shipment Delivery | Shipment delivered to the buyer. |
| `RETURN_REQUEST` | Return Request | Return requested by a buyer. |
| `RETURN_APPROVAL` | Return Approval | Return approved by an administrator. |
| `RETURN_REJECTION` | Return Rejection | Return rejected by an administrator. |
| `INVENTORY_DAMAGE` | Inventory Damage | Units marked as damaged; they can no longer be reserved. |
| `RETURN_RECEPTION` | Return Reception | Returned item received into a warehouse. |
| `REFUND_ISSUANCE` | Refund Issuance | Refund issued to the buyer. |

## Currency

Moneda de los precios.

| Código | Nombre | Descripción |
| --- | --- | --- |
| `COP` | Colombian Peso | Colombian peso. |
| `USD` | United States Dollar | United States dollar. |

## Money

| Atributo | Tipo | Regla |
| --- | --- | --- |
| amount | BigDecimal | Obligatorio, ≥ 0, escala 2 (HALF_UP). |
| currency | Currency | Obligatoria. |

Operaciones: `of`, `zero`, `add`, `subtract` (no produce negativos), `multiply(int)`, `isPositive`, `isGreaterThan`, `hasSameCurrency`.
