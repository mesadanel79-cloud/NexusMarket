# Return and Refund Services (OBJ-11)

## Ciclo
```text
REQUESTED ──approve (ADMIN)──> APPROVED ──receive (LOGISTIC_OPERATOR)──> RECEIVED ──refund (ADMIN)──> REFUNDED
     └──reject (ADMIN)──> REJECTED
```
El pedido original permanece intacto (está finalizado); la devolución es un agregado propio.

## RequestReturnService (BUYER)
* Pedido propio y DELIVERED; SKU de una línea **física** (los digitales no se devuelven).
* `cantidad + ya devuelto (solicitudes no rechazadas) ≤ cantidad comprada`.
* Dentro de `business.return-window-days` (30) desde la entrega; motivo obligatorio (`ReturnReason`).
* Operación `RETURN_REQUEST`.

## ReviewReturnService (ADMIN)
`ApprovalDecision.APPROVED` → APPROVED; `REJECTED` → REJECTED con motivo obligatorio. Operaciones `RETURN_APPROVAL` / `RETURN_REJECTION`.

## ReceiveReturnService (LOGISTIC_OPERATOR)
APPROVED → RECEIVED; las unidades vuelven a la bodega de origen de la línea: como **dañadas** si el motivo es `DEFECTIVE`, si no como disponibles (movimiento `RETURN`). Operación `RETURN_RECEPTION`.

## IssueRefundService (ADMIN)
RECEIVED → REFUNDED; crea `Refund` (`REF-{devolución}`) por `precio unitario × cantidad` y lo registra en la factura (`PARTIALLY_REFUNDED` / `REFUNDED`; nunca supera el total). Operación `REFUND_ISSUANCE`.

## ConsultReturnsService
BUYER: las suyas. ADMIN / SUPERVISOR / LOGISTIC_OPERATOR: por estado.
