# Operation, Audit and Report Services (OBJ-12)

## RegisterOperationAndAuditService — `register(type, performedBy, asset, details)`
Punto único de trazabilidad usado por todos los servicios que cambian estado:
1. `RegisterOperationService` → `Operation` en MySQL (tipo, fecha, usuario, activo afectado).
2. `RegisterAuditLogService` → `AuditLog` en MongoDB (`audit_db.audit_logs`) con `userRole` y `details` (documento variable).
`performedBy` puede ser nulo en procesos del sistema (vencimiento de pedidos).

### Ejemplos de `details`
| Operación | Datos |
| --- | --- |
| `ORDER_CHECKOUT` | buyer, lines, total, status |
| `ORDER_PAYMENT` | paymentMethod, total, invoice |
| `INVENTORY_ADJUSTMENT` | availableBefore, availableAfter, delta, reason |
| `ORDER_EXPIRATION` | reason, expiredAt, buyer |
| `REFUND_ISSUANCE` | refund, amount, invoice, invoiceStatus |

## ConsultOperationsService (SUPERVISOR, ADMIN)
`consultOperations` (SQL) y `consultAuditLog(operationType opcional)` (MongoDB).

## ConsultAdministrativeReportService (SUPERVISOR, ADMIN)
Genera `AdministrativeReport`: usuarios por rol, pedidos/productos/devoluciones por estado, total facturado, total reembolsado y existencias con disponible ≤ `business.low-stock-threshold` (5).
