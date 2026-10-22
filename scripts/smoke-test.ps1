# Prueba de humo de la API de NexusMarket.
# Uso:  powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
param([string]$Base = "http://localhost:8080")

$script:ok = 0
$script:fail = 0

function Call($Method, $Path, $Token, $Body) {
    $headers = @{}
    if ($Token) { $headers["Authorization"] = "Bearer $Token" }
    $params = @{ Method = $Method; Uri = "$Base$Path"; Headers = $headers; UseBasicParsing = $true }
    if ($null -ne $Body) {
        $params["ContentType"] = "application/json"
        $params["Body"] = ($Body | ConvertTo-Json -Depth 6)
    }
    try {
        $r = Invoke-WebRequest @params
        $data = $null
        if ($r.Content) { $data = $r.Content | ConvertFrom-Json }
        return [pscustomobject]@{ Status = [int]$r.StatusCode; Data = $data; Message = "" }
    } catch {
        $status = 0
        $msg = $_.Exception.Message
        if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode }
        if ($_.ErrorDetails -and $_.ErrorDetails.Message) { $msg = $_.ErrorDetails.Message }
        return [pscustomobject]@{ Status = $status; Data = $null; Message = $msg }
    }
}

function Check($Name, $Res, $Expected) {
    if ($Res.Status -eq $Expected) {
        Write-Host "PASS  $Name ($($Res.Status))" -ForegroundColor Green
        $script:ok++
    } else {
        Write-Host "FAIL  $Name -> esperado $Expected, obtuvo $($Res.Status): $($Res.Message)" -ForegroundColor Red
        $script:fail++
    }
}

function Login($Email) {
    $r = Call POST "/api/v1/auth/login" $null @{ email = $Email; password = "Password123*" }
    if ($r.Status -ne 200) {
        Write-Host "No se pudo iniciar sesion con $Email : $($r.Message)" -ForegroundColor Red
        Write-Host "La aplicacion esta corriendo en $Base y el seeder termino?" -ForegroundColor Yellow
        exit 1
    }
    return $r.Data.token
}

Write-Host "`n== 1. Inicio de sesion de los 5 roles ==" -ForegroundColor Cyan
$admin = Login "admin@nexusmarket.com"
$sup = Login "supervisor@nexusmarket.com"
$logi = Login "logistica@nexusmarket.com"
$buyer = Login "comprador@nexusmarket.com"
$seller = Login "vendedor@nexusmarket.com"
Write-Host "PASS  5 tokens obtenidos" -ForegroundColor Green
$script:ok++

Write-Host "`n== 2. Seguridad por rol ==" -ForegroundColor Cyan
Check "sin token a /buyer" (Call GET "/api/v1/buyer/orders" $null) 401
Check "token falso a /buyer" (Call GET "/api/v1/buyer/orders" "esto.no.es.un.token") 401
Check "catalogo publico sin token" (Call GET "/api/v1/public/products" $null) 200
Check "comprador a /admin" (Call GET "/api/v1/admin/users" $buyer) 403
Check "vendedor a /logistics" (Call GET "/api/v1/logistics/orders" $seller) 403
Check "supervisor a /admin" (Call GET "/api/v1/admin/users" $sup) 403
Check "admin a /admin" (Call GET "/api/v1/admin/users" $admin) 200
Check "supervisor a /supervisor" (Call GET "/api/v1/supervisor/users" $sup) 200
Check "ruta inexistente" (Call GET "/api/v1/nada" $admin) 404

Write-Host "`n== 3. Validaciones y errores ==" -ForegroundColor Cyan
Check "cantidad 0 en carrito (400)" (Call POST "/api/v1/buyer/cart/items" $buyer @{ productId = 1; quantity = 0 }) 400
Check "metodo incorrecto (405)" (Call DELETE "/api/v1/public/products" $null) 405
Check "id no numerico (400)" (Call GET "/api/v1/seller/inventory/product/abc" $seller) 400
Check "login con clave mala (401)" (Call POST "/api/v1/auth/login" $null @{ email = "admin@nexusmarket.com"; password = "mala" }) 401

Write-Host "`n== 4. Registro de comprador ==" -ForegroundColor Cyan
$suffix = Get-Random -Minimum 10000 -Maximum 99999
$newBuyer = @{
    document = "9$(Get-Random -Minimum 10000000 -Maximum 99999999)"
    fullName = "Comprador Prueba $suffix"
    email = "prueba$suffix@nexusmarket.com"
    password = "Password123*"
    primaryAddress = "Calle 1 # 2-3, Medellin"
}
Check "registro nuevo (201)" (Call POST "/api/v1/auth/register/buyer" $null $newBuyer) 201
Check "registro repetido (409)" (Call POST "/api/v1/auth/register/buyer" $null $newBuyer) 409

Write-Host "`n== 5. Compra completa del comprador ==" -ForegroundColor Cyan
$products = Call GET "/api/v1/public/products" $null
$productId = @($products.Data)[0].id
Write-Host "Producto usado: $productId"
$addr = "Calle 10 # 20-30, Medellin"
Check "registrar direccion" (Call POST "/api/v1/buyer/addresses" $buyer @{ address = $addr }) 201
Check "agregar al carrito" (Call POST "/api/v1/buyer/cart/items" $buyer @{ productId = $productId; quantity = 1 }) 201
$order = Call POST "/api/v1/buyer/orders/confirm" $buyer @{ deliveryAddress = $addr }
Check "confirmar pedido" $order 200
if ($order.Status -ne 200) {
    Write-Host "Sin pedido confirmado no se puede seguir con el flujo de compra. Revisa el mensaje FAIL de arriba." -ForegroundColor Yellow
} else {
    $orderId = $order.Data.id
    Check "pagar pedido" (Call POST "/api/v1/buyer/orders/$orderId/pay" $buyer @{ paymentApproved = $true }) 200
    Check "consultar factura" (Call GET "/api/v1/buyer/orders/$orderId/invoice" $buyer) 200
    Check "mis pedidos" (Call GET "/api/v1/buyer/orders" $buyer) 200

    Write-Host "`n== 6. Despacho (operador logistico) ==" -ForegroundColor Cyan
    $ship = Call POST "/api/v1/logistics/orders/$orderId/shipment/prepare" $logi
    Check "preparar envio" $ship 201
    if ($ship.Status -eq 201) {
        $sid = $ship.Data.id
        Check "empacar" (Call POST "/api/v1/logistics/shipments/$sid/pack" $logi) 200
        Check "despachar" (Call POST "/api/v1/logistics/shipments/$sid/dispatch" $logi @{ carrier = "Servientrega"; trackingNumber = "TRK-$suffix" }) 200
        Check "en transito" (Call POST "/api/v1/logistics/shipments/$sid/in-transit" $logi) 200
        Check "entregar" (Call POST "/api/v1/logistics/shipments/$sid/deliver" $logi) 200
        Check "comprador ve su envio" (Call GET "/api/v1/buyer/orders/$orderId/shipment" $buyer) 200

        Write-Host "`n== 7. Devolucion y reembolso ==" -ForegroundColor Cyan
        $qty = @{}
        $qty["$productId"] = 1
        $ret = Call POST "/api/v1/buyer/returns" $buyer @{ orderId = $orderId; quantities = $qty; reason = "Producto no cumple expectativas" }
        Check "solicitar devolucion" $ret 201
        if ($ret.Status -eq 201) {
            $retId = $ret.Data.id
            Check "admin aprueba" (Call POST "/api/v1/admin/returns/$retId/approve" $admin) 200
            Check "logistica recibe" (Call POST "/api/v1/logistics/returns/$retId/receive" $logi) 200
            $refunds = Call GET "/api/v1/admin/refunds" $admin
            Check "admin lista reembolsos" $refunds 200
            $refund = @($refunds.Data) | Where-Object { $_.returnRequestId -eq $retId } | Select-Object -First 1
            if ($refund) {
                Check "admin procesa reembolso" (Call POST "/api/v1/admin/refunds/$($refund.id)/process" $admin) 200
            } else {
                Write-Host "FAIL  no aparecio un reembolso para la devolucion $retId" -ForegroundColor Red
                $script:fail++
            }
        }
    }
}

Write-Host "`n== 8. Vendedor ==" -ForegroundColor Cyan
$prod = Call POST "/api/v1/seller/products" $seller @{ name = "Producto prueba $suffix"; type = "PHYSICAL"; price = 25000 }
Check "registrar producto" $prod 201
if ($prod.Status -eq 201) {
    $newProductId = $prod.Data.id
    Check "agregar variante" (Call POST "/api/v1/seller/products/$newProductId/variants" $seller @{ variant = "Color negro" }) 200
    $wh = Call GET "/api/v1/seller/warehouses" $seller
    Check "bodegas del vendedor" $wh 200
    $whId = @($wh.Data)[0].id
    Check "entrada de inventario" (Call POST "/api/v1/seller/inventory/entries" $seller @{ productId = $newProductId; warehouseId = $whId; quantity = 10 }) 201
    Check "publicar producto" (Call POST "/api/v1/seller/products/$newProductId/publish" $seller) 200
    Check "consultar inventario" (Call GET "/api/v1/seller/inventory/product/$newProductId" $seller) 200
    Check "suspender producto" (Call POST "/api/v1/seller/products/$newProductId/suspend" $seller) 200
}

Write-Host "`n== 9. Administrador y supervisor ==" -ForegroundColor Cyan
$internal = @{
    document = "8$(Get-Random -Minimum 10000000 -Maximum 99999999)"
    fullName = "Supervisor Prueba $suffix"
    email = "sup$suffix@nexusmarket.com"
    rawPassword = "Password123*"
    role = "SUPERVISOR"
}
Check "admin crea usuario interno" (Call POST "/api/v1/admin/users" $admin $internal) 201
Check "reporte de ventas (admin)" (Call GET "/api/v1/admin/reports/sales" $admin) 200
Check "reporte de ventas (supervisor)" (Call GET "/api/v1/supervisor/reports/sales" $sup) 200
Check "facturas (admin)" (Call GET "/api/v1/admin/invoices" $admin) 200
Check "movimientos de inventario" (Call GET "/api/v1/admin/inventory/movements" $admin) 200
Check "envios (supervisor)" (Call GET "/api/v1/supervisor/shipments" $sup) 200

Write-Host "`n=============================================="
Write-Host "RESULTADO: $($script:ok) PASS, $($script:fail) FAIL" -ForegroundColor $(if ($script:fail -eq 0) { "Green" } else { "Yellow" })
if ($script:fail -gt 0) { exit 1 }