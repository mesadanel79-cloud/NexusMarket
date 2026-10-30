# NexusMarket - verificacion completa (Docker + pruebas unitarias + flujo de negocio por la API)
# Uso (desde la carpeta raiz del proyecto, en la terminal de VS Code):
#   powershell -ExecutionPolicy Bypass -File .\scripts\verificar.ps1
# El resultado queda en scripts\resultado-verificacion.txt

$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root
$log = Join-Path $PSScriptRoot "resultado-verificacion.txt"
$tmp = Join-Path $env:TEMP "nexus-verify"
New-Item -ItemType Directory -Force -Path $tmp | Out-Null
"NexusMarket - verificacion $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" | Set-Content -Encoding UTF8 $log

$script:pass = 0
$script:fail = 0
$base = "http://localhost:8080/api/v1"
$pwd8 = "Password123*"

function Log($msg) { Write-Host $msg; Add-Content -Encoding UTF8 -Path $log -Value $msg }

function Check($name, $ok, $detail) {
    if ($ok) { $script:pass++; Log "  [OK]    $name" }
    else { $script:fail++; Log "  [FALLA] $name"; if ($detail) { Log "          $detail" } }
}

# Llama a la API con curl.exe y devuelve @{ Status; Body; Json }
function Api($method, $path, $body, $token) {
    $out = Join-Path $tmp "out.json"
    if (Test-Path $out) { Remove-Item $out -Force }
    $cargs = @("-s", "-o", $out, "-w", "%{http_code}", "-X", $method, "$base$path", "-H", "Accept: application/json")
    if ($token) { $cargs += @("-H", "Authorization: Bearer $token") }
    if ($null -ne $body) {
        $in = Join-Path $tmp "in.json"
        [System.IO.File]::WriteAllText($in, ($body | ConvertTo-Json -Depth 10), (New-Object System.Text.UTF8Encoding($false)))
        $cargs += @("-H", "Content-Type: application/json", "--data-binary", "@$in")
    }
    $code = & curl.exe @args
    $text = if (Test-Path $out) { [System.IO.File]::ReadAllText($out) } else { "" }
    $json = $null
    try { if ($text) { $json = $text | ConvertFrom-Json } } catch {}
    return @{ Status = [int]$code; Body = $text; Json = $json }
}

function Short($r) { "HTTP $($r.Status) $($r.Body)".Substring(0, [Math]::Min(400, "HTTP $($r.Status) $($r.Body)".Length)) }

# ---------------------------------------------------------------- 1. Docker
Log ""
Log "== 1. Docker (MySQL, MongoDB y la aplicacion)"
docker info *> $null
if ($LASTEXITCODE -ne 0) { Log "  [FALLA] Docker Desktop no esta corriendo. Abrelo y espera 'Engine running'."; exit 1 }
docker compose up -d --build 2>&1 | ForEach-Object { Add-Content -Encoding UTF8 -Path $log -Value "    $_" }
Log "  Esperando a que la API responda (puede tardar varios minutos la primera vez)..."
$up = $false
for ($i = 0; $i -lt 120; $i++) {
    $h = & curl.exe -s -o NUL -w "%{http_code}" "http://localhost:8080/actuator/health"
    if ($h -eq "200") { $up = $true; break }
    Start-Sleep -Seconds 5
}
Check "La aplicacion arranco (actuator/health = 200)" $up "Revisa: docker logs nexus-app --tail 80"
if (-not $up) {
    Log "---- ultimas lineas de nexus-app ----"
    docker logs nexus-app --tail 80 2>&1 | ForEach-Object { Add-Content -Encoding UTF8 -Path $log -Value "    $_" }
    exit 1
}

# ---------------------------------------------------------------- 2. Pruebas unitarias (mvn test en un contenedor aparte)
Log ""
Log "== 2. Pruebas unitarias (mvn test)"
$src = (Join-Path $root "nexusmarket")
$testOut = docker run --rm -v "${src}:/src:ro" -v nexusmarket_maven_cache:/root/.m2 maven:3.9-eclipse-temurin-17 `
    sh -c "cp -r /src /build && cd /build && rm -rf target && mvn -B test" 2>&1
$testOut | Select-String -Pattern "Tests run:|FAIL|ERROR\]|BUILD" | ForEach-Object { Add-Content -Encoding UTF8 -Path $log -Value "    $_" }
Check "mvn test termina en BUILD SUCCESS" (($testOut -join "`n") -match "BUILD SUCCESS") "Ver lineas de arriba"

# ---------------------------------------------------------------- 3. Flujo de negocio por la API
Log ""
Log "== 3. Flujo de negocio por la API"
$s = (Get-Date).ToString("MMddHHmmss")      # sufijo unico: se puede ejecutar varias veces

$r = Api GET "/catalog/products" $null $null
Check "Catalogo publico sin token (200)" ($r.Status -eq 200) (Short $r)

$tokens = @{}
foreach ($u in @("admin.nexus", "operator.nexus", "supervisor.nexus", "seller.tecnoandes")) {
    $r = Api POST "/auth/login" @{ username = $u; password = $pwd8 } $null
    $tokens[$u] = $r.Json.token
    Check "Login usuario semilla $u (200 + token)" ($r.Status -eq 200 -and $r.Json.token) (Short $r)
}
$admin = $tokens["admin.nexus"]; $operator = $tokens["operator.nexus"]; $supervisor = $tokens["supervisor.nexus"]

$r = Api POST "/auth/login" @{ username = "admin.nexus"; password = "clave-incorrecta" } $null
Check "Login con clave incorrecta es rechazado (401)" ($r.Status -eq 401) (Short $r)

# --- Incorporacion: el admin registra vendedor + primera bodega
$wh = "WH-V$s"
$sellerUser = "vendedor$s"
$r = Api POST "/admin/sellers" @{
    identification = "8$s"; name = "Tienda Prueba $s"; email = "vendedor$s@mail.com"; phoneNumber = "60$s"
    address = "Calle 50 #40-10 Medellin"; storeName = "Tienda $s"; username = $sellerUser; password = $pwd8
    firstWarehouse = @{ code = $wh; name = "Bodega $s"; city = "Medellin"; address = "Carrera 80 #30-20" }
} $admin
Check "Admin registra vendedor con su primera bodega (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300) (Short $r)

$r = Api POST "/admin/sellers" @{
    identification = "7$s"; name = "Intruso"; email = "intruso$s@mail.com"; phoneNumber = "61$s"
    address = "Calle 1"; storeName = "X"; username = "intruso$s"; password = $pwd8
    firstWarehouse = @{ code = "WH-X$s"; name = "X"; city = "X"; address = "X" }
} $tokens["seller.tecnoandes"]
Check "Un vendedor NO puede registrar vendedores (403)" ($r.Status -eq 403) (Short $r)

$r = Api POST "/auth/login" @{ username = $sellerUser; password = $pwd8 } $null
$seller = $r.Json.token
Check "Login del vendedor nuevo (200)" ($r.Status -eq 200 -and $seller) (Short $r)

# --- Catalogo: el vendedor crea 3 productos (2 fisicos y 1 digital)
$skuA = "LAP-$s"; $skuB = "MOU-$s"; $skuC = "EBK-$s"
$prods = @(
    @{ name = "Laptop $s"; description = "Laptop de prueba"; category = "ELECTRONICS"; productType = "PHYSICAL"; variants = @(@{ sku = $skuA; description = "8GB"; price = 2000000; currency = "COP" }) },
    @{ name = "Mouse $s"; description = "Mouse de prueba"; category = "ELECTRONICS"; productType = "PHYSICAL"; variants = @(@{ sku = $skuB; description = "Negro"; price = 80000; currency = "COP" }) },
    @{ name = "eBook $s"; description = "Libro digital"; category = "BOOKS"; productType = "DIGITAL"; variants = @(@{ sku = $skuC; description = "PDF"; price = 40000; currency = "COP" }) }
)
foreach ($p in $prods) {
    $r = Api POST "/seller/products" $p $seller
    Check "Vendedor crea producto $($p.name) ($($p.productType)) (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300) (Short $r)
}

# --- Inventario
$r = Api POST "/seller/inventory/inflows" @{ warehouseId = $wh; sku = $skuA; quantity = 10 } $seller
Check "Ingreso de inventario laptop: 10 unidades (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300) (Short $r)
$r = Api POST "/seller/inventory/inflows" @{ warehouseId = $wh; sku = $skuB; quantity = 20 } $seller
Check "Ingreso de inventario mouse: 20 unidades (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300) (Short $r)
$r = Api POST "/seller/inventory/inflows" @{ warehouseId = "WH-MKT-01"; sku = "LAPTOP-14-8GB"; quantity = 5 } $seller
Check "Vendedor NO puede mover inventario de productos ajenos (403/409)" ($r.Status -eq 403 -or $r.Status -eq 409 -or $r.Status -eq 400) (Short $r)

# --- Comprador: registro
$buyerUser = "comprador$s"
$buyerBody = @{
    identification = "1$s"; name = "Comprador $s"; email = "comprador$s@mail.com"; phoneNumber = "30$s"
    address = "Calle 10 #20-30 Medellin"; additionalAddresses = @("Carrera 70 #44-12 Medellin"); username = $buyerUser; password = $pwd8
}
$r = Api POST "/auth/register/buyer" $buyerBody $null
Check "Registro de comprador (201)" ($r.Status -eq 201) (Short $r)
$r = Api POST "/auth/register/buyer" $buyerBody $null
Check "Documento/correo/usuario repetido es rechazado (409)" ($r.Status -eq 409) (Short $r)

$r = Api POST "/auth/login" @{ username = $buyerUser; password = $pwd8 } $null
$buyer = $r.Json.token
Check "Login del comprador (200 + token)" ($r.Status -eq 200 -and $buyer) (Short $r)

# --- Carrito con 3 productos
$r = Api POST "/buyer/cart/items" @{ sku = $skuA; quantity = 1 } $null
Check "Carrito sin token es rechazado (401)" ($r.Status -eq 401) (Short $r)
foreach ($it in @(@{ sku = $skuA; quantity = 2 }, @{ sku = $skuB; quantity = 1 }, @{ sku = $skuC; quantity = 1 })) {
    $r = Api POST "/buyer/cart/items" $it $buyer
    Check "Agregar $($it.sku) x$($it.quantity) al carrito (200)" ($r.Status -eq 200) (Short $r)
}
$r = Api GET "/buyer/cart" $null $buyer
Check "El carrito tiene 3 productos" ($r.Status -eq 200 -and @($r.Json.items).Count -eq 3) (Short $r)
$r = Api POST "/buyer/cart/items" @{ sku = $skuA; quantity = 999 } $buyer
Check "No se puede pedir mas stock del disponible (409)" ($r.Status -eq 409) (Short $r)

# --- Checkout y pago
$r = Api POST "/buyer/orders" @{ shippingAddress = "Direccion que no es del comprador 123" } $buyer
Check "Checkout con direccion ajena es rechazado (409/400)" ($r.Status -eq 409 -or $r.Status -eq 400) (Short $r)
$r = Api POST "/buyer/orders" @{ shippingAddress = "Carrera 70 #44-12 Medellin" } $buyer
$orderId = $r.Json.orderId
Check "Checkout reserva inventario -> PENDING_PAYMENT" ($r.Status -ge 200 -and $r.Status -lt 300 -and $r.Json.status -eq "PENDING_PAYMENT") (Short $r)
$r = Api POST "/buyer/orders/$orderId/payment" @{ paymentMethod = "CREDIT_CARD" } $buyer
Check "Pago -> PAID" ($r.Status -eq 200 -and $r.Json.status -eq "PAID") (Short $r)
$r = Api GET "/buyer/invoices" $null $buyer
Check "Se genero la factura" ($r.Status -eq 200 -and @($r.Json).Count -ge 1) (Short $r)

# --- RG-03: nadie opera fuera de su rol
$r = Api GET "/admin/users" $null $buyer
Check "Comprador NO entra a rutas de administrador (403)" ($r.Status -eq 403) (Short $r)
$r = Api GET "/buyer/cart" $null $seller
Check "Vendedor NO entra a rutas de comprador (403)" ($r.Status -eq 403) (Short $r)
$r = Api POST "/admin/warehouses" @{ code = "WH-S$s"; name = "X"; city = "X"; address = "X" } $supervisor
Check "Supervisor (solo consulta) NO puede crear bodegas (403)" ($r.Status -eq 403) (Short $r)

# --- Logistica
$r = Api GET "/logistics/orders/ready-to-ship" $null $operator
Check "El pedido aparece listo para despachar" ($r.Status -eq 200 -and $orderId -and ($r.Body -match $orderId)) (Short $r)
$r = Api POST "/logistics/orders/$orderId/shipment" @{ carrier = "Servientrega"; trackingNumber = "SRV-$s" } $operator
$shipmentId = $r.Json.shipmentId
Check "Despacho del pedido (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300 -and $shipmentId) (Short $r)
$r = Api PATCH "/logistics/shipments/$shipmentId/delivery" $null $operator
Check "Confirmacion de entrega (200)" ($r.Status -eq 200) (Short $r)
$r = Api GET "/buyer/orders/$orderId" $null $buyer
Check "El pedido quedo DELIVERED (finalizado)" ($r.Status -eq 200 -and $r.Json.status -eq "DELIVERED") (Short $r)
$r = Api PATCH "/buyer/orders/$orderId/cancel" $null $buyer
Check "Un pedido finalizado NO se puede modificar (409)" ($r.Status -eq 409) (Short $r)

# --- Devolucion y reembolso (Comprador + Admin)
$r = Api POST "/buyer/returns" @{ orderId = $orderId; sku = $skuA; quantity = 1; reason = "DEFECTIVE"; comment = "No enciende" } $buyer
$returnId = $r.Json.returnId
Check "Comprador solicita devolucion (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300 -and $returnId) (Short $r)
$r = Api PATCH "/admin/returns/$returnId/review" @{ decision = "APPROVED" } $admin
Check "Admin aprueba la devolucion (200)" ($r.Status -eq 200) (Short $r)
$r = Api PATCH "/logistics/returns/$returnId/reception" $null $operator
Check "Operador recibe la devolucion en bodega (200)" ($r.Status -eq 200) (Short $r)
$r = Api POST "/admin/returns/$returnId/refund" $null $admin
Check "Admin emite el reembolso (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300) (Short $r)

# --- Consulta, auditoria y sesion
$r = Api GET "/supervisor/reports/summary" $null $supervisor
Check "Supervisor consulta el reporte administrativo (200)" ($r.Status -eq 200) (Short $r)
$r = Api GET "/admin/audit-logs" $null $admin
Check "La bitacora de auditoria tiene registros" ($r.Status -eq 200 -and $r.Body.Length -gt 2) (Short $r)
$r = Api POST "/auth/logout" $null $buyer
Check "Logout del comprador (2xx)" ($r.Status -ge 200 -and $r.Status -lt 300) (Short $r)
$r = Api GET "/buyer/cart" $null $buyer
Check "El token deja de servir despues del logout (401)" ($r.Status -eq 401) (Short $r)

# ---------------------------------------------------------------- Resumen
Log ""
Log "== RESUMEN: $($script:pass) correctas, $($script:fail) fallidas"
Log "   Usuario comprador creado: $buyerUser / $pwd8"
if ($script:fail -gt 0) { exit 1 } else { exit 0 }
