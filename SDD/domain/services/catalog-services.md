# Catalog Services (Dominio 5)

## CreateProductService (SELLER)
1. El vendedor autenticado debe estar ACTIVE.
2. Cada SKU debe ser único en todo el marketplace.
3. `product.publish()`: nombre, categoría, tipo y al menos una variante; precios > 0 y misma moneda. Estado inicial **PUBLISHED** (visible en el catálogo público, flujo §6.1 "Publicación").
4. Operación `PRODUCT_CREATION`.

## ManageProductVariantsService (SELLER dueño)
* `addVariant`: SKU único; no sobre productos descontinuados. Operación `PRODUCT_VARIANT_ADDITION`.
* `changeVariantPrice`: precio > 0, misma moneda. Operación `PRODUCT_PRICE_CHANGE`.

## ChangeProductStatusService (SELLER dueño)
| Método | Transición | Operación |
| --- | --- | --- |
| `suspend` | PUBLISHED → SUSPENDED | `PRODUCT_SUSPENSION` |
| `reactivate` | SUSPENDED → PUBLISHED | `PRODUCT_REACTIVATION` |
| `discontinue` | PUBLISHED / SUSPENDED → DISCONTINUED (final) | `PRODUCT_DISCONTINUATION` |

## ConsultCatalogService
* `consultPublished(category)` y `consultPublishedProduct` (público): solo productos PUBLISHED.
* `consultSellerProducts` (SELLER): sus productos en cualquier estado.
* `consultAll` (SUPERVISOR, ADMIN).

## Reglas
* Productos **físicos** requieren inventario y despacho; **digitales** no manejan inventario y se entregan inmediatamente tras el pago.
