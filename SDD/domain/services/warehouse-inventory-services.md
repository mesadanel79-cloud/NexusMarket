# Warehouse and Inventory Services (Dominios 4 y 6)

## Bodegas
### RegisterWarehouseService (ADMIN)
* Código `[A-Z0-9-]{3,20}` único, nombre, ciudad, dirección y tipo obligatorios.
* `MARKETPLACE`: sin propietario, almacena productos de cualquier vendedor.
* `SELLER`: propietario obligatorio (vendedor existente), almacena solo productos de ese vendedor.
* Operación `WAREHOUSE_REGISTRATION`.

### ChangeWarehouseStatusService (ADMIN)
ACTIVE ↔ INACTIVE. Una bodega inactiva no recibe ingresos ni permite reservas.

### ConsultWarehousesService (ADMIN, LOGISTIC_OPERATOR, SUPERVISOR)

## Inventario distribuido
Toda existencia (`InventoryItem`) está vinculada obligatoriamente a **un producto (variante) y una bodega**; su identificador es `STK-{bodega}-{sku}`.

| Movimiento (`MovementType`) | Origen | Efecto |
| --- | --- | --- |
| `INFLOW` (Ingreso) | `RegisterInventoryInflowService` | disponible += n |
| `RESERVATION` (Reserva) | `CheckoutService` | disponible −= n, reservado += n |
| `RESERVATION_RELEASE` | `CancelOrderService`, `ExpirePendingOrdersService` | reservado −= n, disponible += n |
| `SALE` (Salida por venta) | `PayOrderService` | reservado −= n |
| `ADJUSTMENT` (Ajuste) | `AdjustInventoryService.adjust` / `markDamaged` | disponible = conteo / disponible → dañado |
| `RETURN` (Devolución) | `ReceiveReturnService` | disponible += n, o dañado += n si el motivo es `DEFECTIVE` |

> `RESERVATION_RELEASE` es una extensión necesaria: sin ella el stock reservado por pedidos no pagados se perdería.

### RegisterInventoryInflowService (LOGISTIC_OPERATOR o SELLER dueño del producto)
1. Rol → producto por SKU → propiedad (si es SELLER) → bodega.
2. Producto no descontinuado y **físico**; la bodega debe estar activa y poder almacenarlo (`canStore`).
3. Crea la existencia en el primer ingreso. Movimiento `INFLOW`, operación `INVENTORY_INFLOW`.

### AdjustInventoryService (LOGISTIC_OPERATOR o SELLER dueño)
* `adjust(item, nuevoDisponible, motivo)`: conteo físico, nunca negativo; movimiento `ADJUSTMENT` con la diferencia.
* `markDamaged(item, n, motivo)`: pasa unidades disponibles a dañadas (**no se pueden reservar**). Operación `INVENTORY_DAMAGE`.

### InventoryAllocationService (interno)
* Regla de asignación: cada línea física se reserva completa en la bodega **ACTIVA** con mayor disponible que la cubra. Si ninguna la cubre → `InsufficientStockException`.
* `reserve`, `release`, `confirmSale`, `restock` registran un `InventoryMovement` por cambio.

### Validaciones críticas (§11)
* No se puede reservar inventario inexistente o marcado como dañado.
* No se permiten existencias negativas bajo ninguna circunstancia.
