# NexusMarket — Construcción de Software 2 (2026-2)

Backend del marketplace **NexusMarket** en **Java 17 + Spring Boot 4.1** con **arquitectura hexagonal** (DDD + Ports & Adapters), construido a partir de la *Especificación Funcional del Negocio* (`SDD/enunciado.md`) y con la misma estructura del proyecto base del curso (`bank`). Incluye puertos por rol, JWT con invalidación de tokens, persistencia relacional en **MySQL** y bitácora de auditoría en **MongoDB**.

> **Para ponerlo en marcha paso a paso (Windows + VS Code), ver [`SETUP.md`](SETUP.md).**

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje / Build | Java 17, Maven (wrapper `nexusmarket/mvnw`, o contenedor Docker) |
| Framework | Spring Boot 4.1, Spring MVC, Spring Security (JWT stateless, BCrypt), Validation, Actuator |
| Relacional | Spring Data JPA / Hibernate → MySQL 8 (`nexus_db`, puerto 3306, DDL automático) |
| Auditoría NoSQL | Spring Data MongoDB → `audit_db.audit_logs` (puerto 27017) |
| JWT | JJWT 0.12.6 (`sub`, `jti`, `ver`, `iat`, `exp` + claims para reconstruir el `User`) |
| Pruebas | JUnit 5 — dominio y servicios con dobles en memoria (no requieren base de datos) |

## Estructura del repositorio

```text
.
├── nexusmarket/              # Aplicación Spring Boot (código + tests + resources)
│   └── src/main/java/application/
│       ├── domain/           # Núcleo puro: models, valueobjects, enums, exceptions,
│       │                     #   ports/in (5 roles + acceso público), ports/out, services
│       ├── adapters/         # useCases, rest (controllers, dtos, mappers, exception),
│       │                     #   persistence (jpa, mongodb), security, config
│       └── infrastructure/   # config (registro de servicios), seed, scheduling
├── SDD/                      # Especificación: enunciado, rúbrica, arquitectura, dominio,
│                             #   servicios por subdominio, adapters, C4 (Mermaid)
├── requests/nexusmarket.http # Escenario completo para la extensión REST Client de VS Code
├── docker-compose.yml        # App + MySQL 3306 + MongoDB 27017
├── SETUP.md                  # Guía paso a paso
└── LICENSE
```

## Roles y puertos de entrada

| Rol | Puerto | Rutas | Responsabilidades (matriz §12 de la especificación) |
|---|---|---|---|
| Público | `PublicAccessPort` | `/api/v1/auth`, `/api/v1/catalog` | Login, registro de compradores, catálogo publicado |
| Comprador | `BuyerPort` | `/api/v1/buyer` | Perfil y direcciones, carrito, pedidos, pago, facturas, envíos, devoluciones |
| Vendedor | `SellerPort` | `/api/v1/seller` | Productos y variantes, estados, inventario de sus productos, ventas |
| Operador logístico | `LogisticOperatorPort` | `/api/v1/logistics` | Inventario en bodegas, despacho, entrega, recepción de devoluciones |
| Administrador | `AdminPort` | `/api/v1/admin` | Vendedores (+ primera bodega), bodegas, usuarios, compradores, devoluciones y reembolsos |
| Supervisor | `SupervisorPort` | `/api/v1/supervisor` | Solo consulta: reportes, pedidos, productos, inventario, bitácora |

## Reglas de negocio clave
- Un único rol por usuario; identificación y correo únicos.
- Los vendedores solo los registra el Administrador (junto con su primera bodega).
- Bodegas del Marketplace (cualquier vendedor) y de Vendedor (solo sus productos).
- Inventario distribuido por bodega: Ingreso, Reserva, Salida por venta, Ajuste, Devolución; **nunca negativo** y lo **dañado no se reserva**.
- Pedido: `CART → PENDING_PAYMENT → PAID → SHIPPED → DELIVERED`; un pedido finalizado es inmutable. Digitales: entrega inmediata tras el pago. Pedidos no pagados vencen a los 60 min y liberan el stock.
- Factura por pedido (IVA incluido 19 %), devoluciones dentro de 30 días y reembolsos registrados en la factura.
- Cada operación queda en `operations` (MySQL) y en `audit_logs` (MongoDB).

## Usuarios semilla (contraseña `Password123*`)

| Usuario | Rol |
|---|---|
| `admin.nexus` | ADMIN |
| `supervisor.nexus` | SUPERVISOR |
| `operator.nexus` | LOGISTIC_OPERATOR |
| `seller.tecnoandes` | SELLER (bodega `WH-TEC-01`) |
| `buyer.laura` | BUYER |

Datos: bodega `WH-MKT-01`, productos `PRD-SEED-LAPTOP` (SKU `LAPTOP-14-8GB`, `LAPTOP-14-16GB`) y `PRD-SEED-EBOOK` (digital, `EBOOK-HEX-JAVA`).

## Variables de entorno

| Variable | Default | Uso |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/nexus_db?...` | JDBC MySQL (en Compose: `mysql-db`) |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / `root_password` | Credenciales MySQL |
| `MONGODB_URI` | `mongodb://localhost:27017/audit_db` | MongoDB |
| `JWT_SECRET` | valor académico público | Clave HMAC del JWT (cámbiala fuera de tu máquina) |
| `JWT_EXPIRATION_MS` | `3600000` | Vigencia del token |
| `PAYMENT_EXPIRATION_MINUTES` | `60` | Vencimiento de pedidos no pagados |
| `TAX_RATE` / `RETURN_WINDOW_DAYS` / `LOW_STOCK_THRESHOLD` | `0.19` / `30` / `5` | Parámetros de negocio |
| `SERVER_PORT` | `8080` | Puerto HTTP |
| `FRONTEND_ORIGIN` | `http://localhost:5173` | CORS |

## Pruebas

```powershell
cd nexusmarket
.\mvnw.cmd test
```
Las pruebas unitarias cubren invariantes del dominio (Money, catálogos, usuarios, inventario, pedidos), el flujo completo de compra → despacho → entrega → devolución → reembolso, la matriz de autorización y los mappers de persistencia/REST.
