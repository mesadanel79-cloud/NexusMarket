# REST Security & CORS

* Autenticación **JWT stateless** (sin sesión, CSRF deshabilitado).
* Claims: `sub` (userId), `jti`, `iat`, `exp`, `ver` (authTokenVersion), `username`, `role`, `status`, `identification`, `name`, `email`, `buyerId`, `sellerId`. Con ellos `JwtClaimsMapper` reconstruye el `User` de dominio que reciben los casos de uso.
* `JwtAuthenticationFilter`: token válido + usuario existente, ACTIVE y con `ver` vigente; logout, cambio de contraseña o de estado invalidan los tokens anteriores.
* Autorización por prefijo: `/api/v1/buyer/**`→BUYER, `/api/v1/seller/**`→SELLER, `/api/v1/logistics/**`→LOGISTIC_OPERATOR, `/api/v1/supervisor/**`→SUPERVISOR, `/api/v1/admin/**`→ADMIN. Públicos: `POST /api/v1/auth/login`, `POST /api/v1/auth/register/buyer`, `GET /api/v1/catalog/**`, `/actuator/health`, preflight `OPTIONS`.
* Sin token → 401; rol incorrecto → 403 (JSON).
* CORS: origen permitido `FRONTEND_ORIGIN` (default `http://localhost:5173`), métodos GET/POST/PUT/PATCH/DELETE/OPTIONS, encabezados `Authorization`, `Content-Type`, `X-Request-Id`.
* Contraseñas con BCrypt (`BCryptPasswordServiceAdapter`).
