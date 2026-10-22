# User, Buyer and Seller Services (Dominios 1, 2 y 3)

## LoginService — `login(User credentials)`
* Busca por `username`; compara la contraseña con `PasswordServicePort.matches`.
* Usuario inexistente o contraseña errada → `InvalidCredentialsException` (401). Usuario BLOCKED → `UnauthorizedOperationException` (403).
* Emite el JWT con `JwtServicePort.generateToken(user)` y devuelve `AuthenticationResult`.

## LogoutService — `logout(User)`
Incrementa `authTokenVersion`; todos los tokens emitidos antes quedan inválidos (el filtro compara `ver`).

## RegisterBuyerService — `register(User newUser)` (público)
1. `newUser.buyer` obligatorio. Identificación y correo del comprador únicos.
2. `buyer.register()` → estado comercial ACTIVE; valida contacto y direcciones adicionales (máx. 5).
3. Rol forzado a BUYER; se copian identidad y contacto al `User`.
4. `UserRegistrationRulesService`: username, identificación y correo únicos entre usuarios; contraseña ≥ 8; se cifra.
5. Persiste comprador y usuario. Operaciones: `BUYER_REGISTRATION`, `USER_REGISTRATION`.

## RegisterSellerService — `register(User admin, User newUser, Warehouse firstWarehouse)` (solo ADMIN)
Regla de negocio: *los vendedores no pueden auto-registrarse*. Flujo §6.1 "Incorporación: el Administrador registra al vendedor y su primera bodega".
1. `requireRole(admin, ADMIN)`; vendedor y primera bodega obligatorios.
2. Identificación y correo del vendedor únicos; `seller.register()`.
3. Usuario SELLER con las mismas reglas de registro.
4. La primera bodega se registra con tipo **SELLER** y propietario = vendedor (`RegisterWarehouseService`).
5. Operaciones: `SELLER_REGISTRATION`, `USER_REGISTRATION`, `WAREHOUSE_REGISTRATION`.

## RegisterStaffUserService (solo ADMIN)
Solo roles LOGISTIC_OPERATOR, SUPERVISOR o ADMIN; sin perfiles comerciales.

## ChangeUserStatusService (solo ADMIN)
ACTIVE ↔ BLOCKED, invalida tokens. Un administrador no puede cambiar su propio estado.

## ChangePasswordService (cualquier usuario)
Verifica la contraseña actual, exige ≥ 8 caracteres, cifra e invalida tokens.

## UpdateBuyerProfileService (BUYER)
Actualiza contacto y direcciones adicionales; la identificación no cambia; si cambia el correo debe seguir siendo único. Sincroniza los datos del `User`.

## ChangeBuyerStatusService (ADMIN) / ChangeSellerStatusService (ADMIN)
ACTIVE ↔ SUSPENDED. Un comprador suspendido no puede comprar; un vendedor suspendido no publica productos y sus productos no se agregan al carrito.

## Excepciones
`UserAlreadyExistsException`, `InvalidUserException`, `InvalidPersonDataException`, `InvalidBuyerException`, `InvalidSellerException`, `InvalidStatusTransitionException`, `UnauthorizedOperationException`, `InvalidCredentialsException`.
