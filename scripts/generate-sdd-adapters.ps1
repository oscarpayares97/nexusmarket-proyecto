# Genera SDD\adapters\*.md leyendo el codigo real de los adaptadores.
# Uso:  powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-adapters.ps1
param([string]$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path)

$mainSrc  = Join-Path $Root "nexusmarket\src\main\java\application"
$adapters = Join-Path $mainSrc "adapters"
$outDir   = Join-Path $Root "SDD\adapters"
if (-not (Test-Path $adapters)) { Write-Host "No existe $adapters" -ForegroundColor Red; exit 1 }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$utf8 = New-Object System.Text.UTF8Encoding($false)
$bt = [string][char]96
$SL = [System.Text.RegularExpressions.RegexOptions]::Singleline

function Code($s) { return $bt + $s + $bt }
function Read-File($path) { return (Get-Content -Raw -Encoding UTF8 $path) }
function Write-Doc($name, $lines) { [System.IO.File]::WriteAllText((Join-Path $outDir $name), ($lines -join "`n"), $utf8) }

function Split-Params($s) {
    $parts = @(); $depth = 0; $cur = ""
    foreach ($ch in $s.ToCharArray()) {
        if ($ch -eq '<' -or $ch -eq '(') { $depth++ } elseif ($ch -eq '>' -or $ch -eq ')') { $depth-- }
        if ($ch -eq ',' -and $depth -eq 0) { $parts += $cur.Trim(); $cur = "" } else { $cur += $ch }
    }
    if ($cur.Trim()) { $parts += $cur.Trim() }
    return $parts
}

function Get-Records($text) {
    $result = @()
    foreach ($m in [regex]::Matches($text, 'public\s+record\s+(\w+)\s*\(((?:[^()]|\([^()]*\))*)\)')) {
        $comps = @()
        foreach ($p in (Split-Params $m.Groups[2].Value)) {
            $anns = @()
            foreach ($a in [regex]::Matches($p, '@\w+(?:\([^)]*\))?')) { $anns += $a.Value }
            $rest = ([regex]::Replace($p, '@\w+(?:\([^)]*\))?', '') -replace '\s+', ' ').Trim()
            $idx = $rest.LastIndexOf(' ')
            if ($idx -gt 0) { $comps += ,@($rest.Substring(0, $idx), $rest.Substring($idx + 1), ($anns -join " ")) }
        }
        $result += ,@($m.Groups[1].Value, $comps)
    }
    return $result
}

$statusCode = @{ "OK" = "200"; "CREATED" = "201"; "NO_CONTENT" = "204"; "BAD_REQUEST" = "400"; "UNAUTHORIZED" = "401";
                 "FORBIDDEN" = "403"; "NOT_FOUND" = "404"; "METHOD_NOT_ALLOWED" = "405"; "CONFLICT" = "409";
                 "INTERNAL_SERVER_ERROR" = "500" }
$rolePrefix = [ordered]@{ "/api/v1/auth" = "Public"; "/api/v1/public" = "Public"; "/api/v1/buyer" = "BUYER";
                          "/api/v1/seller" = "SELLER"; "/api/v1/logistics" = "LOGISTICS_OPERATOR";
                          "/api/v1/admin" = "ADMINISTRATOR"; "/api/v1/supervisor" = "SUPERVISOR" }

# ============================================================ 1. Api-rest-endpoints
$ep = New-Object System.Collections.Generic.List[string]
$ep.Add("# REST API Endpoints")
$ep.Add("")
$ep.Add("Base path " + (Code "/api/v1") + ". JSON bodies. Authentication with a JWT in the header " + (Code "Authorization: Bearer <token>") + " (obtained with " + (Code "POST /api/v1/auth/login") + "). The path prefix defines the role required by " + (Code "SecurityConfig") + "; in addition, every domain service validates the role of the actor. Generated from the controllers.")
$ep.Add("")
$ep.Add("Controllers only depend on the input ports (" + (Code "application.domain.ports.in") + ") and convert with DTOs and mappers; they never call domain services directly.")
$ep.Add("")
$ep.Add("---")
$ep.Add("")
$endpointCount = 0
$controllers = Get-ChildItem (Join-Path $adapters "rest\controllers") -Filter *.java | Sort-Object Name
$controllerSummary = @()
foreach ($file in $controllers) {
    $text = Read-File $file.FullName
    $base = ""
    if ($text -match '@RequestMapping\("([^"]*)"\)') { $base = $Matches[1] }
    $role = "Authenticated"
    foreach ($k in $rolePrefix.Keys) { if ($base.StartsWith($k)) { $role = $rolePrefix[$k] } }
    $ep.Add("## " + $file.BaseName)
    $ep.Add("")
    $ep.Add("Base " + (Code $base) + " - access: **$role**")
    $ep.Add("")
    $ep.Add("| Verb | Path | Port operation | Request body | Success | Controller method |")
    $ep.Add("|---|---|---|---|---|---|")
    $pattern = '@(Get|Post|Put|Delete|Patch)Mapping(?:\("([^"]*)"\))?\s*(?:@ResponseStatus\(HttpStatus\.(\w+)\)\s*)?public\s+([\w<>\[\], ?]+?)\s+(\w+)\s*\((.*?)\)\s*\{'
    $ms = [regex]::Matches($text, $pattern, $SL)
    for ($i = 0; $i -lt $ms.Count; $i++) {
        $m = $ms[$i]
        $verb = $m.Groups[1].Value.ToUpper()
        $path = $base + $m.Groups[2].Value
        $status = if ($m.Groups[3].Success -and $m.Groups[3].Value) { $statusCode[$m.Groups[3].Value] } else { "200" }
        $method = $m.Groups[5].Value
        $body = "-"
        if ($m.Groups[6].Value -match '@RequestBody\s+(\w+)') { $body = Code $Matches[1] }
        $endStart = $m.Index + $m.Length
        $endStop = if ($i + 1 -lt $ms.Count) { $ms[$i + 1].Index } else { $text.Length }
        $segment = $text.Substring($endStart, $endStop - $endStart)
        $op = "-"
        if ($segment -match '\b\w*[pP]ort\.(\w+)\(') { $op = Code $Matches[1] }
        $ep.Add("| $verb | " + (Code $path) + " | $op | $body | $status | " + (Code $method) + " |")
        $endpointCount++
    }
    $ep.Add("")
    $controllerSummary += ("{0,-28} {1,3} endpoints" -f $file.BaseName, $ms.Count)
}
$ep.Add("---")
$ep.Add("")
$ep.Add("## Response codes")
$ep.Add("")
$ep.Add("| Code | When |")
$ep.Add("|---|---|")
$ep.Add("| 200 / 201 / 204 | Success / created / success without body |")
$ep.Add("| 400 | Invalid data or malformed JSON |")
$ep.Add("| 401 | Missing or invalid token, or wrong credentials |")
$ep.Add("| 403 | Role without permission for the operation |")
$ep.Add("| 404 | Resource or route not found |")
$ep.Add("| 405 | HTTP method not supported for the route |")
$ep.Add("| 409 | Business rule violated (insufficient stock, invalid status transition, duplicate) |")
$ep.Add("| 500 | Unexpected error |")
$ep.Add("")
Write-Doc "Api-rest-endpoints.md" $ep

# ============================================================ 2. Use-cases-adapters
$uc = New-Object System.Collections.Generic.List[string]
$uc.Add("# Use Case Adapters")
$uc.Add("")
$uc.Add("Package " + (Code "application.adapters.useCases") + ". One class per input port (role). A use case implements the port and **only delegates** to domain services; it has no business logic. Each class is annotated with " + (Code "@Transactional") + " so a multi-step operation (for example paying an order: reserve stock, invoice, create shipment) is saved completely or rolled back completely. The classes are registered as beans by " + (Code "DomainBeansConfig") + ". Generated from the source code.")
$uc.Add("")
$uc.Add("---")
$uc.Add("")
$useCaseOps = 0
$useCaseFiles = Get-ChildItem (Join-Path $adapters "useCases") -Filter *.java | Sort-Object Name
foreach ($file in $useCaseFiles) {
    $text = Read-File $file.FullName
    $name = $file.BaseName
    $port = "-"
    if ($text -match 'class\s+\w+\s+implements\s+(\w+)') { $port = $Matches[1] }
    $tx = if ($text -match '(?m)^@Transactional') { "yes" } else { "no" }
    $fields = @{}
    foreach ($f in [regex]::Matches($text, 'private\s+final\s+(\w+)\s+(\w+)\s*;')) { $fields[$f.Groups[2].Value] = $f.Groups[1].Value }
    $uc.Add("## $name")
    $uc.Add("")
    $uc.Add("- **Implements:** " + (Code $port))
    $uc.Add("- **Transactional:** $tx")
    $uc.Add("- **Injected services:** " + $fields.Count)
    $uc.Add("")
    $uc.Add("| Operation | Delegates to |")
    $uc.Add("|---|---|")
    foreach ($m in [regex]::Matches($text, '@Override\s+public\s+([\w<>\[\], ?]+?)\s+(\w+)\s*\((.*?)\)\s*\{(.*?)\n    \}', $SL)) {
        $op = $m.Groups[2].Value
        $target = "-"
        if ($m.Groups[4].Value -match '(\w+)\.(\w+)\(') {
            $svc = $fields[$Matches[1]]
            if (-not $svc) { $svc = $Matches[1] }
            $target = (Code ($svc + "." + $Matches[2]))
        }
        $uc.Add("| " + (Code $op) + " | $target |")
        $useCaseOps++
    }
    $uc.Add("")
    $uc.Add("---")
    $uc.Add("")
}
Write-Doc "Use-cases-adapters.md" $uc

# ============================================================ 3. Persistence-adapters
$pa = New-Object System.Collections.Generic.List[string]
$pa.Add("# Persistence Adapters")
$pa.Add("")
$pa.Add("Package " + (Code "application.adapters.persistence.jpa") + ". Each adapter implements an output port of the domain with Spring Data JPA over MySQL. The domain never sees JPA: entities are converted with static mappers (" + (Code "*JpaMapper") + "). Enumerations are stored as text (" + (Code "name()") + ") and money as " + (Code "DECIMAL(19,2)") + ". Generated from the source code.")
$pa.Add("")
$pa.Add("---")
$pa.Add("")
$pa.Add("## Adapters")
$pa.Add("")
$pa.Add("| Adapter | Implements (output port) | Spring Data repositories | Mappers |")
$pa.Add("|---|---|---|---|")
$adapterFiles = Get-ChildItem (Join-Path $adapters "persistence\jpa") -Filter *.java | Sort-Object Name
$adapterCount = 0
foreach ($file in $adapterFiles) {
    $text = Read-File $file.FullName
    if ($text -notmatch 'class\s+(\w+)\s+implements\s+([^{]+)\{') { continue }
    $ports = (($Matches[2] -split ',') | ForEach-Object { Code $_.Trim() }) -join ", "
    $repos = @(); foreach ($r in [regex]::Matches($text, '\bSpringDataJpa\w+Repository\b')) { if ($repos -notcontains $r.Value) { $repos += $r.Value } }
    $maps = @(); foreach ($r in [regex]::Matches($text, '\b\w+JpaMapper\b')) { if ($maps -notcontains $r.Value) { $maps += $r.Value } }
    $pa.Add("| " + (Code $file.BaseName) + " | $ports | " + (($repos | ForEach-Object { Code $_ }) -join ", ") + " | " + (($maps | ForEach-Object { Code $_ }) -join ", ") + " |")
    $adapterCount++
}
$pa.Add("")
$pa.Add("## Tables (JPA entities)")
$pa.Add("")
$entityCount = 0
$entityDir = Join-Path $adapters "persistence\jpa\entities"
foreach ($file in (Get-ChildItem $entityDir -Filter *.java | Sort-Object Name)) {
    $text = Read-File $file.FullName
    $isEntity = $text -match '@Entity'
    $table = "(embedded in its parent table)"
    if ($text -match '@Table\(\s*name\s*=\s*"(\w+)"') { $table = $Matches[1] }
    $kind = if ($isEntity) { "Entity" } else { "Embeddable" }
    $pa.Add("### " + $file.BaseName)
    $pa.Add("")
    $pa.Add("- **Kind:** $kind")
    $pa.Add("- **Table:** " + (Code $table))
    $pa.Add("")
    $pa.Add("| Column / attribute | Java type |")
    $pa.Add("|---|---|")
    foreach ($f in [regex]::Matches($text, '(?m)^\s*private\s+(?:static\s+)?(?:final\s+)?([\w<>\[\], ?]+?)\s+(\w+)\s*(?:=[^;]*)?;')) {
        $pa.Add("| " + (Code $f.Groups[2].Value) + " | " + (Code $f.Groups[1].Value.Trim()) + " |")
    }
    $pa.Add("")
    if ($isEntity) { $entityCount++ }
}
$pa.Add("## Spring Data repositories")
$pa.Add("")
$pa.Add("| Repository | Entity |")
$pa.Add("|---|---|")
$repoDir = Join-Path $adapters "persistence\jpa\repositories"
foreach ($file in (Get-ChildItem $repoDir -Filter *.java | Sort-Object Name)) {
    $text = Read-File $file.FullName
    if ($text -match 'interface\s+(\w+)\s+extends\s+JpaRepository<(\w+)') {
        $pa.Add("| " + (Code $Matches[1]) + " | " + (Code $Matches[2]) + " |")
    }
}
$pa.Add("")
Write-Doc "Persistence-adapters.md" $pa

# ============================================================ 4. Rest-adapters
$ra = New-Object System.Collections.Generic.List[string]
$ra.Add("# REST Adapters")
$ra.Add("")
$ra.Add("Package " + (Code "application.adapters.rest") + ". Translates HTTP into calls to the input ports and domain objects into JSON. Controllers do not contain business rules. Generated from the source code.")
$ra.Add("")
$ra.Add("---")
$ra.Add("")
$ra.Add("## Controllers")
$ra.Add("")
$ra.Add("See " + (Code "Api-rest-endpoints.md") + " for every route.")
$ra.Add("")
foreach ($c in $controllerSummary) { $ra.Add("- " + (Code $c.Trim())) }
$ra.Add("")
foreach ($sub in @("requests", "responses")) {
    $ra.Add("## " + (Get-Culture).TextInfo.ToTitleCase($sub) + " DTOs")
    $ra.Add("")
    foreach ($file in (Get-ChildItem (Join-Path $adapters "rest\dtos\$sub") -Filter *.java | Sort-Object Name)) {
        $text = Read-File $file.FullName
        $recs = Get-Records $text
        if ($recs.Count -gt 0) {
            foreach ($r in $recs) {
                $ra.Add("### " + $r[0])
                $ra.Add("")
                $ra.Add("| Field | Type |")
                $ra.Add("|---|---|")
                foreach ($c in $r[1]) { $ra.Add("| " + (Code $c[1]) + " | " + (Code $c[0]) + " |") }
                $ra.Add("")
            }
        } else {
            $ra.Add("### " + $file.BaseName)
            $ra.Add("")
            $ra.Add("| Field | Type |")
            $ra.Add("|---|---|")
            foreach ($f in [regex]::Matches($text, '(?m)^\s*private\s+(?:static\s+)?(?:final\s+)?([\w<>\[\], ?]+?)\s+(\w+)\s*(?:=[^;]*)?;')) {
                $ra.Add("| " + (Code $f.Groups[2].Value) + " | " + (Code $f.Groups[1].Value.Trim()) + " |")
            }
            $ra.Add("")
        }
    }
}
$ra.Add("## Mappers")
$ra.Add("")
foreach ($file in (Get-ChildItem (Join-Path $adapters "rest\mappers") -Filter *.java | Sort-Object Name)) {
    $text = Read-File $file.FullName
    $ra.Add("### " + $file.BaseName)
    $ra.Add("")
    foreach ($m in [regex]::Matches($text, 'public\s+static\s+([\w<>\[\], ?]+?)\s+(\w+)\s*\(([^)]*)\)')) {
        $ra.Add("- " + (Code ($m.Groups[1].Value.Trim() + " " + $m.Groups[2].Value + "(" + (($m.Groups[3].Value -replace '\s+', ' ').Trim()) + ")")))
    }
    $ra.Add("")
}
Write-Doc "Rest-adapters.md" $ra

# ============================================================ 5. Rest-validation
$rv = New-Object System.Collections.Generic.List[string]
$rv.Add("# REST Validation")
$rv.Add("")
$rv.Add("Request bodies are validated with Jakarta Bean Validation (" + (Code "@Valid") + " in the controller). A violation is translated by " + (Code "GlobalExceptionHandler") + " into HTTP 400 with the message " + (Code "field: reason") + ". Business rules are not validated here: they live in the domain and answer 409. Generated from the request DTOs.")
$rv.Add("")
$rv.Add("---")
$rv.Add("")
$constraintCount = 0
foreach ($file in (Get-ChildItem (Join-Path $adapters "rest\dtos\requests") -Filter *.java | Sort-Object Name)) {
    $text = Read-File $file.FullName
    $recs = Get-Records $text
    if ($recs.Count -gt 0) {
        foreach ($r in $recs) {
            $rv.Add("## " + $r[0])
            $rv.Add("")
            $rv.Add("| Field | Type | Constraints |")
            $rv.Add("|---|---|---|")
            foreach ($c in $r[1]) {
                $cons = if ($c[2]) { Code $c[2] } else { "-" }
                if ($c[2]) { $constraintCount++ }
                $rv.Add("| " + (Code $c[1]) + " | " + (Code $c[0]) + " | $cons |")
            }
            $rv.Add("")
        }
    } else {
        $rv.Add("## " + $file.BaseName)
        $rv.Add("")
        $rv.Add("| Field | Type | Constraints |")
        $rv.Add("|---|---|---|")
        foreach ($f in [regex]::Matches($text, '((?:@\w+(?:\([^)]*\))?\s*)+)private\s+([\w<>]+)\s+(\w+)\s*;')) {
            $anns = ($f.Groups[1].Value -replace '\s+', ' ').Trim()
            $constraintCount++
            $rv.Add("| " + (Code $f.Groups[3].Value) + " | " + (Code $f.Groups[2].Value) + " | " + (Code $anns) + " |")
        }
        $rv.Add("")
    }
}
Write-Doc "Rest-validation.md" $rv

# ============================================================ 6. Global-exception-handler
$ge = New-Object System.Collections.Generic.List[string]
$ge.Add("# Global Exception Handler")
$ge.Add("")
$ge.Add("Class " + (Code "application.adapters.rest.exception.GlobalExceptionHandler") + " (" + (Code "@RestControllerAdvice") + "). Translates domain and framework exceptions into a uniform JSON response. Spring selects the most specific handler. Generated from the source code.")
$ge.Add("")
$ge.Add("## Response body")
$ge.Add("")
$ge.Add("| Field | Meaning |")
$ge.Add("|---|---|")
$ge.Add("| " + (Code "timestamp") + " | Moment of the error |")
$ge.Add("| " + (Code "status") + " | HTTP status code |")
$ge.Add("| " + (Code "error") + " | HTTP reason phrase |")
$ge.Add("| " + (Code "message") + " | Human readable message (the domain exception message) |")
$ge.Add("| " + (Code "path") + " | Requested path |")
$ge.Add("| " + (Code "requestId") + " | Value of the " + (Code "X-Request-Id") + " header or a generated UUID, also written to the log |")
$ge.Add("")
$ge.Add("## Exception mapping")
$ge.Add("")
$ge.Add("| Exception | HTTP | Handler method |")
$ge.Add("|---|---|---|")
$handlerCount = 0
$geText = Read-File (Join-Path $adapters "rest\exception\GlobalExceptionHandler.java")
$hp = '@ExceptionHandler\(\s*(?:\{([^}]*)\}|([\w.]+))\s*\)\s*public\s+[\w<>, ?]+\s+(\w+)\s*\(.*?\)\s*\{(.*?)\n    \}'
foreach ($m in [regex]::Matches($geText, $hp, $SL)) {
    $excs = if ($m.Groups[1].Success -and $m.Groups[1].Value) { $m.Groups[1].Value } else { $m.Groups[2].Value }
    $excList = (($excs -split ',') | ForEach-Object { Code ($_.Trim() -replace '\.class$', '') }) -join ", "
    $st = "-"
    if ($m.Groups[4].Value -match 'HttpStatus\.(\w+)') { $st = $statusCode[$Matches[1]]; if (-not $st) { $st = $Matches[1] } }
    $ge.Add("| $excList | $st | " + (Code $m.Groups[3].Value) + " |")
    $handlerCount++
}
$ge.Add("")
$ge.Add("Domain exceptions not listed explicitly fall to " + (Code "DomainException") + " and answer **409 Conflict** (a valid request rejected by a business rule).")
$ge.Add("")
Write-Doc "Global-exception-handler.md" $ge

# ============================================================ 7. Rest-security-cors
$sc = New-Object System.Collections.Generic.List[string]
$sc.Add("# REST Security and CORS")
$sc.Add("")
$sc.Add("Package " + (Code "application.adapters.security") + ". Stateless security with JWT; there is no server session and therefore no logout service (the client discards the token). Generated from the source code.")
$sc.Add("")
$sc.Add("## Access rules by route (SecurityConfig)")
$sc.Add("")
$sc.Add("| Route pattern | Access |")
$sc.Add("|---|---|")
$secText = Read-File (Join-Path $adapters "security\SecurityConfig.java")
$ruleCount = 0
foreach ($m in [regex]::Matches($secText, '\.requestMatchers\(([^)]*)\)\s*\.(permitAll\(\)|hasRole\("(\w+)"\)|authenticated\(\))')) {
    $access = if ($m.Groups[3].Success -and $m.Groups[3].Value) { "role " + (Code $m.Groups[3].Value) } elseif ($m.Groups[2].Value -like "permitAll*") { "public" } else { "authenticated" }
    $sc.Add("| " + (Code ($m.Groups[1].Value -replace '\s+', ' ').Trim()) + " | $access |")
    $ruleCount++
}
$sc.Add("| " + (Code "any other request") + " | authenticated |")
$sc.Add("")
$sc.Add("Unauthenticated requests answer **401** and requests with an insufficient role answer **403**, both as JSON.")
$sc.Add("")
$sc.Add("## CORS")
$sc.Add("")
$sc.Add("| Setting | Value |")
$sc.Add("|---|---|")
foreach ($m in [regex]::Matches($secText, 'configuration\.set(Allowed\w+|ExposedHeaders)\((.*)\);')) {
    $sc.Add("| " + (Code $m.Groups[1].Value) + " | " + (Code ($m.Groups[2].Value -replace '\s+', ' ').Trim()) + " |")
}
foreach ($m in [regex]::Matches($secText, '\$\{(FRONTEND_ORIGIN[^}]*)\}')) {
    $sc.Add("| Property | " + (Code $m.Groups[1].Value) + " |")
}
$sc.Add("")
$sc.Add("## JWT (JwtProvider)")
$sc.Add("")
$jwtFile = Join-Path $adapters "security\JwtProvider.java"
if (Test-Path $jwtFile) {
    $jwt = Read-File $jwtFile
    $claims = @(); foreach ($m in [regex]::Matches($jwt, '\.claim\("(\w+)"')) { $claims += $m.Groups[1].Value }
    $props = @(); foreach ($m in [regex]::Matches($jwt, '\$\{([^}:]+)')) { $props += $m.Groups[1].Value }
    $sc.Add("- **Algorithm:** HMAC-SHA (symmetric key)")
    $sc.Add("- **Standard claims:** " + (Code "sub") + " (user id), " + (Code "jti") + ", " + (Code "iat") + ", " + (Code "exp"))
    $sc.Add("- **Custom claims:** " + (($claims | ForEach-Object { Code $_ }) -join ", "))
    $sc.Add("- **Configuration properties:** " + (($props | ForEach-Object { Code $_ }) -join ", "))
    $sc.Add("- **Methods:** " + ((([regex]::Matches($jwt, 'public\s+[\w<>\[\], ?]+\s+(\w+)\s*\(') | ForEach-Object { Code $_.Groups[1].Value })) -join ", "))
}
$sc.Add("")
$sc.Add("## Request authentication flow (JwtAuthenticationFilter)")
$sc.Add("")
$sc.Add("1. Read the " + (Code "Authorization: Bearer <token>") + " header; if absent the request continues unauthenticated.")
$sc.Add("2. Validate signature and expiration of the token.")
$sc.Add("3. Extract the user id and load the **persisted** user through " + (Code "LoadAuthenticatedUserService") + ". Design decision: the user is reloaded from the database on every request instead of trusting the claims, so a blocked or deactivated user loses access immediately.")
$sc.Add("4. Wrap the user in " + (Code "AuthenticatedUserPrincipal") + " (authority " + (Code "ROLE_<role>") + ") and store it in the security context.")
$sc.Add("5. Controllers receive the domain user with " + (Code '@AuthenticationPrincipal(expression = "user")') + ".")
$sc.Add("")
$sc.Add("Passwords are stored as BCrypt hashes through " + (Code "BCryptPasswordServiceAdapter") + ".")
$sc.Add("")
Write-Doc "Rest-security-cors.md" $sc

Write-Host "`nGenerado en $outDir" -ForegroundColor Green
$controllerSummary | ForEach-Object { Write-Host $_ }
Write-Host ("TOTAL endpoints REST        : {0}" -f $endpointCount) -ForegroundColor Cyan
Write-Host ("Operaciones en casos de uso : {0}" -f $useCaseOps)
Write-Host ("Adaptadores de persistencia : {0}" -f $adapterCount)
Write-Host ("Entidades JPA               : {0}" -f $entityCount)
Write-Host ("Handlers de excepciones     : {0}" -f $handlerCount)
Write-Host ("Reglas de seguridad         : {0}" -f $ruleCount)
Write-Host ("Campos con validacion       : {0}" -f $constraintCount)