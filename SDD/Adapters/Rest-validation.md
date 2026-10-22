# REST Validation

1. **Sintaxis (adaptador):** Jakarta Bean Validation en los `record` (`@NotBlank`, `@Email`, `@Size`, `@Pattern`, `@Positive`, `@PositiveOrZero`, `@NotEmpty`, `@Valid` anidado) con `@Valid @RequestBody`. Errores → 400 con `campo: mensaje`.
2. **Catálogos:** los códigos (`category`, `productType`, `paymentMethod`, `reason`, `status`, `role`, `warehouseType`, `currency`) se validan con `@Pattern` y luego `fromCode`; un código desconocido en query params → `InvalidCatalogValueException` → 400.
3. **Negocio (dominio):** unicidad, transiciones, stock, propiedad, ventanas de tiempo… → excepciones de dominio → 403/404/409.

Reglas de formato principales:
| Campo | Regla |
| --- | --- |
| email | `@Email`, máx. 100 |
| phoneNumber | `\d{7,15}` |
| identification | obligatorio, máx. 20 |
| username / password | 4–50 / mínimo 8 |
| sku | `[A-Z0-9-]{3,40}` |
| código de bodega | `[A-Z0-9-]{3,20}` |
| additionalAddresses | máximo 5, cada una ≤ 200 |
| price / quantity | > 0 |
