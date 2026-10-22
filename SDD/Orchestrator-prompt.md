# Prompt de orquestación — NexusMarket (adaptado del proyecto base)

Rol: Lead Software Architect. Objetivo: mantener y extender NexusMarket en **Java 17 + Spring Boot** siguiendo estrictamente `SDD/` y la arquitectura hexagonal. No se permite cambiar de stack.

## Entorno
* MySQL 3306 (`nexus_db`, usuario `nexus_app`), MongoDB 27017 (`audit_db.audit_logs`), app 8080. Ver `docker-compose.yml`.

## Fases
1. **Dominio** (`domain/`): modelos, Value Objects, excepciones y puertos según `SDD/domain/*.md`. Sin dependencias de frameworks.
2. **Paralelo:** (2A) persistencia JPA según `Adapters/Persistence-adapters.md`; (2B) auditoría MongoDB; (2C) puertos de entrada por rol según `domain/Input-ports.md`.
3. **Paralelo:** (3A) servicios de dominio y `adapters/useCases` según `domain/Domain Services.md` y `domain/services/*.md`; (3B) DTOs, mappers y controladores según `Adapters/Api-rest-endpoints.md`, `Rest-validation.md`, `Global-exception-handler.md`.
4. **Seguridad:** JWT (`sub`, `jti`, `ver`, claims para reconstruir `User`), filtro, `SecurityConfig` por prefijo de rol, CORS (`Adapters/Rest-security-cors.md`).
5. **Pruebas:** dominio y servicios con dobles en memoria; mappers de adaptadores.

## Criterios de finalización
1. `./mvnw test` en verde.
2. Cumplimiento de reglas: RG-01..03, validaciones críticas §11, matriz §12.
3. Tablas MySQL y colección MongoDB creadas automáticamente al arrancar.
4. `domain/` sin imports de Spring/JPA/Mongo/Jackson/JJWT.
5. Trazabilidad completa: RequestDTO → Domain → UseCase(User del JWT) → Service → Port → Adapter(Entity/Document).
