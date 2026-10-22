# Genera SDD\domain\services\*.md y SDD\domain\Domain Services.md leyendo el codigo real.
# Uso:  powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-services.ps1
param([string]$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path)

$servicesDir = Join-Path $Root "nexusmarket\src\main\java\application\domain\services"
$testsDir    = Join-Path $Root "nexusmarket\src\test\java"
$outDir      = Join-Path $Root "SDD\domain\services"
$indexFile   = Join-Path $Root "SDD\domain\Domain Services.md"

if (-not (Test-Path $servicesDir)) { Write-Host "No existe $servicesDir" -ForegroundColor Red; exit 1 }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$utf8 = New-Object System.Text.UTF8Encoding($false)
$bt = [string][char]96

function Code($s) { return $bt + $s + $bt }
function Words($name) { return ($name -creplace '([a-z0-9])([A-Z])', '$1 $2') }

$areas = [ordered]@{
    "user"          = @{ File = "user-authentication-services.md"; Title = "User and Authentication Management Services" }
    "buyer"         = @{ File = "buyer-services.md";               Title = "Buyer Management Services" }
    "seller"        = @{ File = "seller-services.md";              Title = "Seller Management Services" }
    "catalog"       = @{ File = "catalog-services.md";             Title = "Catalog Management Services" }
    "warehouse"     = @{ File = "warehouse-services.md";           Title = "Warehouse Management Services" }
    "inventory"     = @{ File = "inventory-services.md";           Title = "Inventory Management Services" }
    "order"         = @{ File = "order-services.md";               Title = "Order Management Services" }
    "billing"       = @{ File = "billing-services.md";             Title = "Billing Services" }
    "shipping"      = @{ File = "shipping-services.md";            Title = "Shipping Services" }
    "returns"       = @{ File = "return-services.md";              Title = "Return and Refund Services" }
    "report"        = @{ File = "report-services.md";              Title = "Reporting Services" }
    "authorization" = @{ File = "authorization-services.md";       Title = "Authorization Services" }
}

$descriptions = @{
    "Login" = "Authenticates a user with email and password and issues a signed JWT."
    "RegisterInternalUser" = "Creates an internal system user (administrator, supervisor or logistics operator). Restricted to administrators."
    "ChangeUserStatus" = "Activates, deactivates or blocks a user. An administrator cannot change its own status."
    "ChangeUserPassword" = "Changes the password of the authenticated user after verifying the current one."
    "ConsultUser" = "Lists the system users for back-office roles."
    "LoadAuthenticatedUser" = "Resolves the persisted user that owns a valid token so that every request is evaluated against the current database state."
    "RegisterBuyer" = "Registers a new buyer user together with the buyer profile and its primary delivery address."
    "AddBuyerAddress" = "Adds an additional delivery address to the authenticated buyer."
    "ChangeBuyerCommercialStatus" = "Enables or suspends the ability of a buyer to purchase."
    "RegisterSeller" = "Registers a seller user, its trade name and its first warehouse in a single operation. Restricted to administrators."
    "RegisterProduct" = "Creates a product owned by the authenticated seller."
    "PublishProduct" = "Publishes a product so that it appears in the public catalog."
    "AddProductVariant" = "Adds a variant (for example size or color) to a product of the authenticated seller."
    "SuspendProduct" = "Temporarily removes a product from the public catalog."
    "DiscontinueProduct" = "Permanently retires a product from sale."
    "ConsultPublishedProducts" = "Lists the published products of the catalog. Available without authentication."
    "OwnedProductLoader" = "Internal helper shared by the catalog services: checks that the actor is a seller, loads the product and verifies that the seller owns it."
    "CreateWarehouse" = "Creates a warehouse, either the marketplace warehouse or a warehouse that belongs to a seller."
    "ChangeWarehouseStatus" = "Activates or deactivates a warehouse."
    "ConsultWarehouses" = "Lists the warehouses visible to the requesting role."
    "RegisterInventoryEntry" = "Registers stock entering a warehouse for a product and records the movement."
    "AdjustInventory" = "Sets the available quantity of a product in a warehouse to a corrected value and records the adjustment movement."
    "ChangeInventoryStatus" = "Changes the operational status of an inventory record."
    "ConsultInventory" = "Retrieves the stock of a product across warehouses."
    "ConsultInventoryMovements" = "Lists the inventory movements (entries, reservations, sales, adjustments, returns)."
    "ReserveInventory" = "Reserves stock for the items of an order when the order is confirmed. Internal service."
    "RegisterInventorySale" = "Records the stock exit of the items of an order when its shipment is dispatched. Internal service."
    "RegisterInventoryReturn" = "Returns stock to inventory when a returned item is received. Internal service."
    "InventoryOperationGuard" = "Internal helper shared by the inventory services: validates the role (seller or logistics operator), that the product is physical and the warehouse is active, that a seller only operates its own products and warehouses, and loads or creates the inventory record."
    "AddItemToCart" = "Adds a product to the open cart of the authenticated buyer, creating the cart if it does not exist."
    "RemoveItemFromCart" = "Removes a product from the open cart of the authenticated buyer."
    "ConfirmOrder" = "Confirms the cart as an order with a delivery address and reserves the stock of its items."
    "PayOrder" = "Registers the payment of an order, issues its invoice and delivers digital products immediately."
    "ConsultMyOrders" = "Lists the orders of the authenticated buyer."
    "ConsultOrders" = "Lists every order for back-office roles."
    "IssueInvoice" = "Issues the invoice of a paid order. An order can only be invoiced once. Internal service."
    "ConsultInvoice" = "Retrieves the invoice of an order for its owner or for a back-office role."
    "ConsultInvoices" = "Lists every issued invoice for back-office roles."
    "StartShipmentPreparation" = "Creates the shipment of a paid order and starts its preparation."
    "PackShipment" = "Marks a shipment as packed and ready to be dispatched."
    "DispatchShipment" = "Dispatches a packed shipment with its carrier and tracking number, marks the order as shipped and records the inventory sale."
    "RegisterShipmentInTransit" = "Marks a dispatched shipment as in transit."
    "ConfirmDelivery" = "Confirms the delivery of a shipment and marks the order as delivered."
    "ConsultShipment" = "Retrieves the shipment of an order for its owner or for a back-office role."
    "ConsultShipments" = "Lists every shipment for back-office roles."
    "RequestReturn" = "Creates a return request for delivered items of an order of the authenticated buyer."
    "ApproveReturn" = "Approves a pending return request so that the buyer can send the items back."
    "RejectReturn" = "Rejects a return request recording the rejection reason."
    "ReceiveReturn" = "Registers the physical reception of an approved return, returns the stock to inventory and creates the pending refund."
    "ProcessRefund" = "Processes a pending refund. A refund can only be processed once."
    "ConsultMyReturns" = "Lists the return requests of the authenticated buyer."
    "ConsultReturns" = "Lists every return request for back-office roles."
    "ConsultRefunds" = "Lists every refund for back-office roles."
    "GenerateSalesReport" = "Builds the sales report: gross sales, refunded amount, net sales, orders by status and sales by seller."
    "ValidateRoleAuthorization" = "Verifies that the acting user exists, is active and holds one of the roles allowed for an operation (RG-01, RG-03)."
    "ValidateOwnership" = "Verifies that the acting user is the owner of the resource being accessed."
}

$httpStatus = @{
    "InvalidCredentialsException" = "401"
    "UnauthorizedOperationException" = "403"
    "EntityNotFoundException" = "404"
    "InvalidDataException" = "400"
}

# Carga una sola vez el texto de las pruebas para saber que servicio cubre cada una
$testTexts = @()
foreach ($t in Get-ChildItem -Path $testsDir -Recurse -Filter *.java) {
    $testTexts += [pscustomobject]@{ Name = $t.BaseName; Text = (Get-Content -Raw -Encoding UTF8 $t.FullName) }
}

$index = New-Object System.Collections.Generic.List[string]
$index.Add("# Services")
$index.Add("")
$index.Add("## Introduction")
$index.Add("")
$index.Add("This document gives a conceptual overview of the domain services of the NexusMarket marketplace. Each service is a plain Java class without framework annotations; it receives the authenticated user (the actor), validates its role, applies the business rules and talks to the outside world only through output ports.")
$index.Add("")
$index.Add("The detailed definition of every service (authorization, collaborators, rules, exceptions and tests) is generated from the source code and lives in one document per subdomain under " + (Code "SDD/domain/services/") + ".")
$index.Add("")
$index.Add("---")
$index.Add("")

$total = 0
$noDescription = @()
$noTests = @()
$summary = @()

foreach ($folder in $areas.Keys) {
    $dir = Join-Path $servicesDir $folder
    if (-not (Test-Path $dir)) { Write-Host "Area sin carpeta: $folder" -ForegroundColor Yellow; continue }
    $meta = $areas[$folder]
    $files = Get-ChildItem -Path $dir -Filter *.java | Sort-Object Name

    $md = New-Object System.Collections.Generic.List[string]
    $md.Add("# " + $meta.Title)
    $md.Add("")
    $md.Add("Package " + (Code "application.domain.services.$folder") + ". " + $files.Count + " classes. Generated from the source code.")
    $md.Add("")
    $index.Add("# " + $meta.Title)
    $index.Add("")
    $index.Add("Detail: " + (Code ("services/" + $meta.File)))
    $index.Add("")

    foreach ($file in $files) {
        $cls = $file.BaseName
        $key = $cls -replace 'Service$', ''
        $title = Words $key
        $text = Get-Content -Raw -Encoding UTF8 $file.FullName
        $total++

        $desc = $descriptions[$key]
        if (-not $desc) { $desc = "(add a description)"; $noDescription += $cls }

        $implements = "-"
        if ($text -match 'class\s+\w+\s+implements\s+(\w+)') { $implements = $Matches[1] }

        $roles = @()
        foreach ($m in [regex]::Matches($text, 'requireRole\(\s*\w+\s*,([^;]*)\)')) {
            foreach ($r in [regex]::Matches($m.Groups[1].Value, 'UserRole\.(\w+)')) { $roles += $r.Groups[1].Value }
        }
        $roles = @($roles | Select-Object -Unique)
        if ($roles.Count -gt 0) { $auth = ($roles | ForEach-Object { Code $_ }) -join ", " }
        else { $auth = "No role check (public operation or internal service invoked by other services)" }

        $deps = @()
        if ($text -match "public\s+$cls\s*\(([^)]*)\)") {
            foreach ($p in ($Matches[1] -split ',')) {
                $q = ($p.Trim() -replace '^final\s+', '')
                $type = ($q -split '\s+')[0]
                if ($type) { $deps += $type }
            }
        }

        $ops = @()
        foreach ($m in [regex]::Matches($text, 'public\s+([\w<>\[\], ?]+?)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws[^{]*)?\{')) {
            if ($m.Groups[2].Value -ne $cls) {
                $args2 = ($m.Groups[3].Value -replace '\s+', ' ').Trim()
                $ops += ($m.Groups[1].Value.Trim() + " " + $m.Groups[2].Value + "(" + $args2 + ")")
            }
        }

        $notes = @()
        foreach ($m in [regex]::Matches($text, '//\s*([^\r\n]+)')) {
            $c = $m.Groups[1].Value.Trim()
            if ($c -and ($notes -notcontains $c)) { $notes += $c }
        }

        $exc = [ordered]@{}
        foreach ($m in [regex]::Matches($text, 'new\s+(\w+Exception)\(\s*("([^"]*)")?')) {
            $n = $m.Groups[1].Value
            $msg = $m.Groups[3].Value
            if (-not $exc.Contains($n)) { $exc[$n] = New-Object System.Collections.Generic.List[string] }
            if ($msg -and -not $exc[$n].Contains($msg)) { $exc[$n].Add($msg) }
        }

        $tests = @()
        foreach ($tt in $testTexts) {
            if ($tt.Text -match "\b$cls\b") { $tests += $tt.Name }
        }
        $tests = @($tests | Select-Object -Unique)
        if ($tests.Count -eq 0) { $noTests += $cls }

        $md.Add("## $title")
        $md.Add("")
        $md.Add($desc)
        $md.Add("")
        $md.Add("- **Class:** " + (Code "application.domain.services.$folder.$cls"))
        $md.Add("- **Input port:** " + $(if ($implements -eq "-") { "none (used directly by the role use cases or by other services)" } else { Code $implements }))
        $md.Add("- **Authorization:** $auth")
        foreach ($op in $ops) { $md.Add("- **Operation:** " + (Code $op)) }
        $md.Add("")
        if ($deps.Count -gt 0) {
            $md.Add("**Collaborators (injected through the constructor):**")
            $md.Add("")
            foreach ($d in $deps) { $md.Add("- " + (Code $d)) }
            $md.Add("")
        }
        if ($notes.Count -gt 0) {
            $md.Add("**Business rules and notes (from the code):**")
            $md.Add("")
            foreach ($n in $notes) { $md.Add("- $n") }
            $md.Add("")
        }
        if ($exc.Count -gt 0) {
            $md.Add("**Exceptions:**")
            $md.Add("")
            $md.Add("| Exception | HTTP | Message |")
            $md.Add("|---|---|---|")
            foreach ($name in $exc.Keys) {
                $code = "409"
                if ($httpStatus.ContainsKey($name)) { $code = $httpStatus[$name] }
                $messages = if ($exc[$name].Count -gt 0) { ($exc[$name] -join " / ") } else { "-" }
                $md.Add("| " + (Code $name) + " | $code | $messages |")
            }
            $md.Add("")
        }
        $md.Add("**Covered by tests:** " + $(if ($tests.Count -gt 0) { ($tests | ForEach-Object { Code $_ }) -join ", " } else { "none yet" }))
        $md.Add("")
        $md.Add("---")
        $md.Add("")

        $index.Add("## $title")
        $index.Add("")
        $index.Add($desc)
        $index.Add("")
    }

    [System.IO.File]::WriteAllText((Join-Path $outDir $meta.File), ($md -join "`n"), $utf8)
    $index.Add("---")
    $index.Add("")
    $summary += ("{0,-16} {1,3} classes -> {2}" -f $folder, $files.Count, $meta.File)
}

[System.IO.File]::WriteAllText($indexFile, ($index -join "`n"), $utf8)

Write-Host "`nGenerado en $outDir" -ForegroundColor Green
$summary | ForEach-Object { Write-Host $_ }
Write-Host "TOTAL: $total clases documentadas (54 servicios + 2 auxiliares)" -ForegroundColor Cyan
if ($noDescription.Count -gt 0) { Write-Host "Sin descripcion: $($noDescription -join ', ')" -ForegroundColor Yellow }
if ($noTests.Count -gt 0) { Write-Host "Sin pruebas que los mencionen: $($noTests -join ', ')" -ForegroundColor Yellow }

$known = $areas.Keys
foreach ($d in Get-ChildItem -Path $servicesDir -Directory) {
    if ($known -notcontains $d.Name) { Write-Host "Carpeta de servicios no documentada: $($d.Name)" -ForegroundColor Yellow }
}