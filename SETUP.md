# Guía paso a paso — NexusMarket (Windows + VS Code)

## 0. Prerrequisitos
1. **JDK 17 o superior** (funciona con 17, 21 o 25): `java -version`.
2. **Docker Desktop** (para MySQL y MongoDB): `docker --version`.
3. **VS Code** con las extensiones recomendadas (al abrir la carpeta, VS Code te las sugiere desde `.vscode/extensions.json`): *Extension Pack for Java*, *Spring Boot Extension Pack*, *Docker*, *REST Client*.
4. Puertos libres: 3306 (MySQL), 27017 (MongoDB), 8080 (app).

No necesitas instalar Maven: el proyecto trae el wrapper (`mvnw.cmd`).

## 1. Abrir el proyecto en VS Code
`Archivo → Abrir carpeta…` y selecciona la carpeta raíz del repositorio. Espera a que la extensión de Java importe el proyecto Maven `nexusmarket` (barra inferior: "Java: Ready").

## 2. Opción A — Todo con Docker (recomendado)
```powershell
docker compose up -d --build
docker compose logs -f nexus-app
```
Cuando veas `Started NexusMarketApplication` y `Seed data created`, la API está en `http://localhost:8080`.
```powershell
curl.exe -s http://localhost:8080/actuator/health
```

## 3. Opción B — Bases en Docker y la app desde VS Code
```powershell
docker compose up -d mysql-db mongo-db
cd nexusmarket
.\mvnw.cmd spring-boot:run
```
o en VS Code: pestaña *Run and Debug* → **NexusMarket (local)** (configurado en `.vscode/launch.json`).

## 4. Correr las pruebas
```powershell
cd nexusmarket
.\mvnw.cmd test
```
Resultado esperado: `BUILD SUCCESS`. Las pruebas no necesitan MySQL ni MongoDB.

## 5. Probar la API
Abre `requests/nexusmarket.http` y pulsa **Send Request** sobre cada bloque en orden (login → carrito → checkout → pago → despacho → entrega → devolución → reembolso). Los tokens e ids se encadenan solos.

Errores típicos esperados (sirven para demostrar reglas):
- Comprar más unidades de las disponibles → 409 `Insufficient stock…`.
- Usar una dirección que no es del comprador → 409.
- Supervisor intentando modificar algo → 403.
- Token después de `logout` → 401.

## 6. Ver los datos
```powershell
docker exec nexus-mysql mysql -uroot -proot_password -e "USE nexus_db; SHOW TABLES; SELECT identifier,status,total_amount FROM orders;"
docker exec nexus-mongo mongosh --quiet audit_db --eval "db.audit_logs.find().sort({operationDate:-1}).limit(5)"
```

## 7. Subir a tu GitHub
Desde la carpeta raíz del repositorio (donde están `README.md` y `docker-compose.yml`):
```powershell
git init
git add .
git commit -m "feat: NexusMarket hexagonal architecture (domain, adapters, security, SDD)"
git branch -M main
git remote add origin https://github.com/<tu-usuario>/<tu-repositorio>.git
git push -u origin main
```
Si el repositorio remoto ya tiene commits (por ejemplo un README creado en GitHub): `git pull origin main --allow-unrelated-histories` y luego `git push`.

## 8. Solución de problemas
| Problema | Solución |
|---|---|
| Puerto 3306/27017/8080 ocupado | Detén el servicio local o cambia `HOST_SERVER_PORT` / `SERVER_PORT`. |
| `Communications link failure` | MySQL aún no arrancó: espera el healthcheck (`docker ps`). |
| VS Code marca errores rojos al abrir | Espera a que termine la importación de Maven o ejecuta *Java: Clean Java Language Server Workspace*. |
| `mvnw.cmd` no descarga Maven | Revisa la conexión/proxy; alternativamente usa la Opción A (Docker). |
| Quiero reiniciar los datos | `docker compose down -v` y vuelve a levantar. |
