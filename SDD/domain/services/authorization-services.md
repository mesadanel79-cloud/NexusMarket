# Authorization Services

## Objetivo
Garantizar RG-01 (usuario autenticado), RG-02 (rol único) y RG-03 (nadie administra información fuera de su rol) dentro del dominio, de forma independiente a Spring Security. La seguridad HTTP (por prefijo de ruta) es la primera barrera; estos servicios son la segunda y definitiva.

## ValidateUserAuthorizationService
| Método | Regla | Excepción |
| --- | --- | --- |
| `requireActive(User)` | El usuario existe y su `status` es `ACTIVE`. | `UnauthorizedOperationException` |
| `requireRole(User, UserRole...)` | `requireActive` + el rol está entre los permitidos. | `UnauthorizedOperationException` |

## ValidateOwnershipService
| Método | Regla |
| --- | --- |
| `requireBuyer(User)` | Rol BUYER, perfil enlazado; devuelve el `Buyer` **autoritativo** cargado por `BuyerRepositoryPort`. |
| `requireSeller(User)` | Rol SELLER, perfil enlazado; devuelve el `Seller` autoritativo. |
| `requireOrderOwnership(Buyer, Order)` | El pedido pertenece al comprador. |
| `requireProductOwnership(Seller, Product)` | El producto pertenece al vendedor. |

## InventoryAccessService
| Método | Regla |
| --- | --- |
| `requireInventoryRole(User)` | Rol LOGISTIC_OPERATOR o SELLER. Se ejecuta **antes** de cargar datos. |
| `requireInventoryAdministrator(User, Product)` | Además, si es SELLER, el producto debe ser suyo (matriz §12: el vendedor administra solo el inventario de sus productos). |

## Orden de validación (todos los servicios)
1. Rol y estado del usuario → 2. Carga del recurso → 3. Propiedad → 4. Invariantes del modelo → 5. Persistencia → 6. Operación + Auditoría.

## Restricciones por rol
| Rol | Puede | No puede |
| --- | --- | --- |
| BUYER | Su perfil, carrito, pedidos, facturas, envíos y devoluciones. | Ver datos de otros compradores o inventarios. |
| SELLER | Sus productos, variantes, estados, inventario de sus productos y sus ventas. | Auto-registrarse; tocar productos/inventario de otros vendedores. |
| LOGISTIC_OPERATOR | Inventario de cualquier bodega, despachos, entregas y recepción de devoluciones. | Aprobar devoluciones o reembolsos. |
| ADMIN | Vendedores (+ primera bodega), bodegas, usuarios, estado de compradores, devoluciones y reembolsos, reportes. | Comprar o administrar productos de un vendedor. |
| SUPERVISOR | Solo consulta: reportes, pedidos, productos, bodegas, inventario, devoluciones, operaciones y bitácora. | Cualquier operación que cambie estado. |
