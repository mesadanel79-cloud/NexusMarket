# REST Adapters

* **Controllers** (`adapters/rest/controllers`): un controlador por puerto de entrada (`AuthRestController` y `CatalogRestController` usan `PublicAccessPort`). Reciben el usuario con `@AuthenticationPrincipal(expression = "user") User user`, que es el modelo de dominio reconstruido desde el JWT.
* **DTOs** (`adapters/rest/dtos`): `requests/*RequestDTO` y `responses/*ResponseDTO` como `record` inmutables.
* **Mappers** (`adapters/rest/mappers`): `UserRestMapper`, `ParticipantRestMapper`, `WarehouseRestMapper`, `CatalogRestMapper`, `InventoryRestMapper`, `OrderRestMapper`, `ReturnRestMapper`, `OperationRestMapper`. Convierten `RequestDTO → Domain` y `Domain → ResponseDTO`; los códigos de texto se convierten a Value Objects con `fromCode`.
* **Exception handler**: `GlobalExceptionHandler` (ver documento propio).

Contrato completo de rutas: `Api-rest-endpoints.md`.
