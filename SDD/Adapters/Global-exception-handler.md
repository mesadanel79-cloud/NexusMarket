# Global Exception Handler

`adapters/rest/exception/GlobalExceptionHandler` (`@RestControllerAdvice`). Todas las respuestas de error usan el mismo sobre y devuelven el encabezado `X-Request-Id` (se reutiliza el recibido o se genera uno):

```json
{ "timestamp": "2026-10-21T10:15:30", "status": 409, "error": "CONFLICT",
  "message": "Insufficient stock for SKU LAP-8GB ...", "path": "/api/v1/buyer/orders", "requestId": "..." }
```

| Excepción | HTTP |
| --- | --- |
| `MethodArgumentNotValidException`, `HandlerMethodValidationException`, `HttpMessageNotReadableException`, `InvalidCatalogValueException`, `IllegalArgumentException` | 400 |
| `InvalidCredentialsException`; sin token / token inválido (entry point de seguridad) | 401 |
| `UnauthorizedOperationException`; rol no permitido para la ruta (access denied handler) | 403 |
| `EntityNotFoundException`, `NoResourceFoundException` | 404 |
| `UserAlreadyExistsException` y demás `DomainException` | 409 |
| Cualquier otra | 500 |
