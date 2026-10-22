# Genera Domain Model.md, Domain Value Objects.md y Output-ports.md leyendo el codigo real.
# Uso:  powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-domain.ps1
param([string]$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path)

$mainSrc = Join-Path $Root "nexusmarket\src\main\java\application"
$domain  = Join-Path $mainSrc "domain"
$sddDir  = Join-Path $Root "SDD\domain"
if (-not (Test-Path $domain)) { Write-Host "No existe $domain" -ForegroundColor Red; exit 1 }
New-Item -ItemType Directory -Force -Path $sddDir | Out-Null
$utf8 = New-Object System.Text.UTF8Encoding($false)
$bt = [string][char]96
function Code($s) { return $bt + $s + $bt }

function Split-Params($s) {
    $parts = @(); $depth = 0; $cur = ""
    foreach ($ch in $s.ToCharArray()) {
        if ($ch -eq '<') { $depth++ } elseif ($ch -eq '>') { $depth-- }
        if ($ch -eq ',' -and $depth -eq 0) { $parts += $cur.Trim(); $cur = "" } else { $cur += $ch }
    }
    if ($cur.Trim()) { $parts += $cur.Trim() }
    return $parts
}

function Get-Validations($text) {
    $list = @()
    foreach ($m in [regex]::Matches($text, 'Guard\.(\w+)\(([^;]*)\);')) {
        $list += ("Guard." + $m.Groups[1].Value + "(" + (($m.Groups[2].Value -replace '\s+', ' ').Trim()) + ")")
    }
    foreach ($m in [regex]::Matches($text, 'new\s+(\w+Exception)\(\s*"([^"]*)"')) {
        $list += ($m.Groups[1].Value + ": " + $m.Groups[2].Value)
    }
    return @($list | Select-Object -Unique)
}

function Get-Notes($text) {
    $notes = @()
    foreach ($m in [regex]::Matches($text, '//\s*([^\r\n]+)')) {
        $c = $m.Groups[1].Value.Trim()
        if ($c -and ($notes -notcontains $c)) { $notes += $c }
    }
    return $notes
}

$modelDesc = @{
    "User" = "System identity with a single role (RG-02) and an access status. Identified by its document number and e-mail."
    "Buyer" = "Buyer profile of a user: primary and additional delivery addresses and commercial status."
    "Seller" = "Seller profile of a user, with its trade name."
    "Warehouse" = "Place where stock is kept; it belongs to the marketplace or to a seller."
    "Product" = "Item offered in the catalog by a seller: physical or digital, with price, variants and publication status."
    "Inventory" = "Stock of a product in a warehouse: available and reserved quantities and operational status."
    "InventoryMovement" = "Immutable record of a change in stock (entry, reservation, sale, adjustment or return)."
    "Order" = "Purchase of a buyer. While open it is the cart; afterwards it moves through pending payment, paid, shipped and delivered."
    "OrderItem" = "Line of an order: snapshot of the product name, type, unit price, quantity and warehouse."
    "Invoice" = "Immutable billing document issued once per paid order."
    "Shipment" = "Delivery of a paid order with its carrier, tracking number and lifecycle."
    "ReturnRequest" = "Request of a buyer to return items of a delivered order."
    "ReturnItem" = "Line of a return request: product, quantity and unit price snapshot."
    "Refund" = "Money reimbursed to the buyer for a received return."
    "SalesReport" = "Read model with gross sales, refunds, net sales, orders by status and sales by seller."
    "AuthenticationResult" = "Result of a successful login: the signed token and the authenticated user."
    "Guard" = "Static validation helpers used by entities and value objects (non-blank text, non-null values)."
}

$voDesc = @{
    "Money" = "Monetary amount with exact decimal arithmetic."
    "Email" = "Validated e-mail address."
    "DocumentNumber" = "Validated identity document number."
    "UserRole" = "Role of a user in the marketplace (RG-02: one role per user)."
    "UserStatus" = "Access status of a user; only ACTIVE users can operate."
    "CommercialStatus" = "Whether a buyer is allowed to purchase."
    "ProductType" = "Physical products manage inventory and shipping; digital products are delivered immediately."
    "ProductStatus" = "Publication lifecycle of a product."
    "InventoryStatus" = "Operational status of an inventory record."
    "MovementType" = "Kind of inventory movement."
    "OrderStatus" = "Lifecycle of an order, from cart to delivered."
    "ShipmentStatus" = "Lifecycle of a shipment."
    "ReturnStatus" = "Lifecycle of a return request."
    "RefundStatus" = "Lifecycle of a refund."
    "WarehouseType" = "Whether a warehouse belongs to the marketplace or to a seller."
}

$missing = @()

# ---------------------------------------------------------------- Domain Model
$md = New-Object System.Collections.Generic.List[string]
$md.Add("# Domain Model")
$md.Add("")
$md.Add("Package " + (Code "application.domain.models") + ". Entities and read models of the NexusMarket domain, generated from the source code. Value objects and enumerations are documented in " + (Code "Domain Value Objects.md") + ".")
$md.Add("")
$md.Add("---")
$md.Add("")

$modelFiles = Get-ChildItem (Join-Path $domain "models") -Filter *.java | Sort-Object Name
foreach ($file in $modelFiles) {
    $name = $file.BaseName
    $text = Get-Content -Raw -Encoding UTF8 $file.FullName
    $desc = $modelDesc[$name]
    if (-not $desc) { $desc = "(add a description)"; $missing += $name }

    $md.Add("## $name")
    $md.Add("")
    $md.Add($desc)
    $md.Add("")

    $attrs = @()
    if ($text -match "public\s+record\s+$name\s*\(([^)]*(?:\([^)]*\)[^)]*)*)\)") {
        $md.Add("Declared as a Java " + (Code "record") + " (immutable).")
        $md.Add("")
        foreach ($p in (Split-Params $Matches[1])) {
            $q = ($p -replace '\s+', ' ').Trim()
            $idx = $q.LastIndexOf(' ')
            if ($idx -gt 0) { $attrs += ,@($q.Substring(0, $idx), $q.Substring($idx + 1)) }
        }
    } else {
        foreach ($m in [regex]::Matches($text, '(?m)^\s*private\s+(?:static\s+)?(?:final\s+)?([\w<>\[\], ?]+?)\s+(\w+)\s*(?:=[^;]*)?;')) {
            $attrs += ,@($m.Groups[1].Value.Trim(), $m.Groups[2].Value)
        }
    }
    if ($attrs.Count -gt 0) {
        $md.Add("| Attribute | Type |")
        $md.Add("|---|---|")
        foreach ($a in $attrs) { $md.Add("| " + (Code $a[1]) + " | " + (Code $a[0]) + " |") }
        $md.Add("")
    }

    $ops = @()
    foreach ($m in [regex]::Matches($text, 'public\s+([\w<>\[\], ?]+?)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws[^{]*)?\{')) {
        $n = $m.Groups[2].Value
        if ($n -ne $name -and $n -notmatch '^get[A-Z]' -and $n -notin @("assignId", "equals", "hashCode", "toString")) {
            $args2 = ($m.Groups[3].Value -replace '\s+', ' ').Trim()
            $ops += ($m.Groups[1].Value.Trim() + " " + $n + "(" + $args2 + ")")
        }
    }
    if ($ops.Count -gt 0) {
        $md.Add("**Behavior (business operations):**")
        $md.Add("")
        foreach ($o in $ops) { $md.Add("- " + (Code $o)) }
        $md.Add("")
    }

    $val = Get-Validations $text
    if ($val.Count -gt 0) {
        $md.Add("**Validations and invariants:**")
        $md.Add("")
        foreach ($v in $val) { $md.Add("- " + (Code $v)) }
        $md.Add("")
    }
    $notes = Get-Notes $text
    if ($notes.Count -gt 0) {
        $md.Add("**Notes (from the code):**")
        $md.Add("")
        foreach ($n in $notes) { $md.Add("- $n") }
        $md.Add("")
    }
    $md.Add("---")
    $md.Add("")
}
[System.IO.File]::WriteAllText((Join-Path $sddDir "Domain Model.md"), ($md -join "`n"), $utf8)

# ------------------------------------------------------- Domain Value Objects
$vo = New-Object System.Collections.Generic.List[string]
$vo.Add("# Domain Value Objects")
$vo.Add("")
$vo.Add("Package " + (Code "application.domain.valueobjects") + ". Immutable value objects and enumerations, generated from the source code.")
$vo.Add("")
$vo.Add("---")
$vo.Add("")

$voFiles = Get-ChildItem (Join-Path $domain "valueobjects") -Filter *.java | Sort-Object Name
$records = @(); $enums = @()
foreach ($file in $voFiles) {
    $text = Get-Content -Raw -Encoding UTF8 $file.FullName
    if ($text -match 'public\s+enum\s+') { $enums += $file } else { $records += $file }
}

$vo.Add("# Value objects")
$vo.Add("")
foreach ($file in $records) {
    $name = $file.BaseName
    $text = Get-Content -Raw -Encoding UTF8 $file.FullName
    $desc = $voDesc[$name]
    if (-not $desc) { $desc = "(add a description)"; $missing += $name }
    $vo.Add("## $name")
    $vo.Add("")
    $vo.Add($desc)
    $vo.Add("")
    if ($text -match "public\s+record\s+$name\s*\(([^)]*)\)") {
        $vo.Add("- **Component:** " + (Code (($Matches[1] -replace '\s+', ' ').Trim())))
    } else {
        $vo.Add("- **Kind:** " + (Code "class"))
    }
    $ops = @()
    foreach ($m in [regex]::Matches($text, 'public\s+([\w<>\[\], ?]+?)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws[^{]*)?\{')) {
        if ($m.Groups[2].Value -ne $name) {
            $ops += ($m.Groups[1].Value.Trim() + " " + $m.Groups[2].Value + "(" + (($m.Groups[3].Value -replace '\s+', ' ').Trim()) + ")")
        }
    }
    foreach ($o in $ops) { $vo.Add("- **Operation:** " + (Code $o)) }
    $vo.Add("")
    $val = Get-Validations $text
    if ($val.Count -gt 0) {
        $vo.Add("**Validations:**")
        $vo.Add("")
        foreach ($v in $val) { $vo.Add("- " + (Code $v)) }
        $vo.Add("")
    }
    $vo.Add("---")
    $vo.Add("")
}

$vo.Add("# Enumerations")
$vo.Add("")
$vo.Add("| Enum | Values | Purpose |")
$vo.Add("|---|---|---|")
foreach ($file in $enums) {
    $name = $file.BaseName
    $text = Get-Content -Raw -Encoding UTF8 $file.FullName
    $values = @()
    if ($text -match 'enum\s+\w+\s*\{([^}]*)\}') {
        $body = ($Matches[1] -replace '//[^\r\n]*', '')
        $body = ($body -split ';')[0]
        foreach ($v in ($body -split ',')) {
            $t = ($v -replace '\s+', ' ').Trim()
            if ($t) { $values += (Code $t) }
        }
    }
    $desc = $voDesc[$name]
    if (-not $desc) { $desc = "(add a description)"; $missing += $name }
    $vo.Add("| " + (Code $name) + " | " + ($values -join ", ") + " | $desc |")
}
$vo.Add("")
[System.IO.File]::WriteAllText((Join-Path $sddDir "Domain Value Objects.md"), ($vo -join "`n"), $utf8)

# ---------------------------------------------------------------- Output ports
$implementedBy = @{}
foreach ($f in Get-ChildItem -Path (Join-Path $mainSrc "adapters") -Recurse -Filter *.java) {
    $t = Get-Content -Raw -Encoding UTF8 $f.FullName
    if ($t -match 'class\s+\w+\s+implements\s+([^{]+)\{') {
        foreach ($i in ($Matches[1] -split ',')) {
            $port = ($i.Trim() -split '\s+')[0]
            if (-not $implementedBy.ContainsKey($port)) { $implementedBy[$port] = @() }
            $implementedBy[$port] += $f.BaseName
        }
    }
}

$op = New-Object System.Collections.Generic.List[string]
$op.Add("# Output Ports")
$op.Add("")
$op.Add("Package " + (Code "application.domain.ports.out") + ". Interfaces that the domain uses to reach persistence and security without knowing the technology. Each port lists the adapter that implements it. Generated from the source code.")
$op.Add("")
$op.Add("---")
$op.Add("")
$portFiles = Get-ChildItem (Join-Path $domain "ports\out") -Filter *.java | Sort-Object Name
$withoutAdapter = @()
foreach ($file in $portFiles) {
    $name = $file.BaseName
    $text = Get-Content -Raw -Encoding UTF8 $file.FullName
    $op.Add("## $name")
    $op.Add("")
    if ($name -match '^(\w+)RepositoryPort$') { $op.Add("Persistence port of the " + (Code $Matches[1]) + " aggregate.") }
    elseif ($name -eq "PasswordServicePort") { $op.Add("Password hashing and verification.") }
    elseif ($name -eq "JwtServicePort") { $op.Add("Issuing and parsing of signed tokens.") }
    else { $op.Add("(add a description)"); $missing += $name }
    $op.Add("")
    if ($implementedBy.ContainsKey($name)) {
        $op.Add("**Implemented by:** " + (($implementedBy[$name] | ForEach-Object { Code $_ }) -join ", "))
    } else {
        $op.Add("**Implemented by:** none yet")
        $withoutAdapter += $name
    }
    $op.Add("")
    $op.Add("| Method |")
    $op.Add("|---|")
    foreach ($m in [regex]::Matches($text, '(?m)^\s*([\w<>\[\], ?]+?)\s+(\w+)\s*\(([^)]*)\)\s*;')) {
        $sig = $m.Groups[1].Value.Trim() + " " + $m.Groups[2].Value + "(" + (($m.Groups[3].Value -replace '\s+', ' ').Trim()) + ")"
        $op.Add("| " + (Code $sig) + " |")
    }
    $op.Add("")
    $op.Add("---")
    $op.Add("")
}
[System.IO.File]::WriteAllText((Join-Path $sddDir "Output-ports.md"), ($op -join "`n"), $utf8)

Write-Host "`nGenerado en $sddDir" -ForegroundColor Green
Write-Host ("Domain Model.md           : {0} clases" -f $modelFiles.Count)
Write-Host ("Domain Value Objects.md   : {0} value objects, {1} enums" -f $records.Count, $enums.Count)
Write-Host ("Output-ports.md           : {0} puertos" -f $portFiles.Count)
if ($missing.Count -gt 0) { Write-Host "Sin descripcion: $($missing -join ', ')" -ForegroundColor Yellow }
if ($withoutAdapter.Count -gt 0) { Write-Host "Puertos sin adaptador: $($withoutAdapter -join ', ')" -ForegroundColor Yellow }