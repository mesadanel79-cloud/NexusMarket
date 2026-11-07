# Cart and Order Services (OBJ-07, Dominio 7)

## Ciclo de estados del pedido
```text
CART ──submitForPayment──> PENDING_PAYMENT ──markPaid──> PAID ──markShipped──> SHIPPED ──markDelivered──> DELIVERED
  │                              │                         └──(solo digital) markDelivered──────────────────┘
  └────────────cancel────────────┴──────cancel───────────┴──> CANCELLED   (comprador antes del despacho, o vencimiento del pago)
```
DELIVERED y CANCELLED son **finales e inmutables** (`OrderImmutableException`).

## ManageCartService (BUYER)
* `addItem`: comprador con estado comercial ACTIVE; producto PUBLISHED de vendedor ACTIVE; cantidad > 0 (digitales: 1); para físicos la cantidad acumulada no supera lo que **una sola bodega activa** tiene disponible (`InventoryAllocationService.availableInSingleWarehouse`), que es el mismo criterio de la reserva en el checkout: carrito y checkout nunca se contradicen.
* `updateItem`, `removeItem`. `ConsultCartService.consult` devuelve el carrito (vacío si no existe).

## CheckoutService — `checkout(User, shippingAddress)` (BUYER)
1. Carrito no vacío; `Order.draftFrom(cart, address)` → estado **CART**, precios congelados; la dirección debe ser la principal o una adicional del comprador. Cada línea se revalida con `Product.isAvailableForSale()` (producto PUBLISHED **y vendedor ACTIVE**): si el vendedor fue suspendido después de agregar al carrito, el pedido se rechaza.
2. Persiste y `InventoryAllocationService.reserve` (movimientos `RESERVATION`).
3. `submitForPayment()` → **PENDING_PAYMENT**; vacía el carrito. Operación `ORDER_CHECKOUT`.
4. Todo en una transacción: si falta stock en una línea no queda nada reservado.

## PayOrderService — `pay(User, Order, PaymentMethod)` (BUYER dueño)
1. `markPaid` (PENDING_PAYMENT → PAID). El pago es simulado (no hay pasarela).
2. `confirmSale` (movimientos `SALE`) y `IssueInvoiceService.issue`.
3. Pedido solo digital → `markDelivered` inmediato. Operaciones `ORDER_PAYMENT`, `INVOICE_ISSUANCE`, (`ORDER_DELIVERY`).

## CancelOrderService (BUYER dueño)
Permitido en CART, PENDING_PAYMENT y **PAID** (pagado pero aún no despachado). Operación `ORDER_CANCELLATION`.
* **PENDING_PAYMENT:** no hubo dinero; libera las reservas (`RESERVATION_RELEASE`).
* **PAID:** las unidades nunca salieron de la bodega, así que `InventoryAllocationService.reverseSale` las devuelve a disponible (movimiento `RETURN`), y `Invoice.refundRemaining()` reembolsa el total de la factura (estado **REFUNDED**). Operación adicional `REFUND_ISSUANCE`.
* **SHIPPED / DELIVERED / CANCELLED:** rechazado (409). Después de la entrega el camino es la devolución (`RequestReturnService`).

## ExpirePendingOrdersService (proceso del sistema)
Ejecutado cada minuto por `ExpirePendingOrdersScheduler`. Cancela pedidos en PENDING_PAYMENT con más de `business.payment-expiration-minutes` (60) y libera el stock. Operación `ORDER_EXPIRATION` con `performedBy = null` y motivo en `details`.

## ConsultOrdersService
| Método | Rol |
| --- | --- |
| `consultBuyerOrders`, `consultBuyerOrder` | BUYER (solo los suyos) |
| `consultSellerSales` | SELLER (pedidos con sus productos) |
| `consultOrdersReadyToShip` | LOGISTIC_OPERATOR (PAID con productos físicos) |
| `consultAll`, `consultAnyOrder` | SUPERVISOR, ADMIN (y operador para `consultAnyOrder`) |
