# Invoice and Shipment Services (OBJ-09, OBJ-10)

## IssueInvoiceService (interno, desde el pago)
* Una sola factura por pedido; solo pedidos PAID.
* Precios con IVA incluido: `subtotal = total / (1 + business.tax-rate)` (2 decimales, HALF_UP), `taxAmount = total − subtotal`.
* Estado inicial ISSUED; `refundedAmount = 0`. Operación `INVOICE_ISSUANCE`.
* Ejemplo: total 2.000.000 COP, IVA 19 % → subtotal 1.680.672,27 e IVA 319.327,73.

## ConsultInvoicesService (BUYER)
Facturas propias; consultar una ajena → `UnauthorizedOperationException`.

## DispatchOrderService (LOGISTIC_OPERATOR)
1. Pedido PAID con productos físicos y sin envío previo.
2. `Shipment.dispatch(order, operator, carrier, trackingNumber)` → IN_TRANSIT con la dirección del pedido.
3. `order.markShipped()` → SHIPPED (salida física de la bodega). Operación `SHIPMENT_DISPATCH`.

## ConfirmDeliveryService (LOGISTIC_OPERATOR)
`shipment.confirmDelivery()` → DELIVERED y `order.markDelivered()` → DELIVERED (pedido finalizado). Operaciones `SHIPMENT_DELIVERY` y `ORDER_DELIVERY`.

## ConsultShipmentsService
* BUYER: envío de su pedido (seguimiento).
* LOGISTIC_OPERATOR / SUPERVISOR / ADMIN: envíos por estado.
