# Escribe SDD\C4\*.md (README + 11 documentos) con los datos reales de NexusMarket.
# Uso:  powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-c4.ps1
param([string]$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path)

$dir = Join-Path $Root "SDD\C4"
New-Item -ItemType Directory -Force -Path $dir | Out-Null
$utf8 = New-Object System.Text.UTF8Encoding($false)
$docs = [ordered]@{}

$docs["README.md"] = @'
# C4 Documentation - NexusMarket

C4 model in 4 levels, one sheet per project layer and a flow matrix per endpoint. Diagrams are written in **Mermaid** (rendered by GitHub and by the VS Code Mermaid extension). Facts were verified against the source code; the detailed tables are generated from the code by the scripts in `scripts/generate-sdd-*.ps1`.

## Document map

| # | File | Content |
|---|------|---------|
| - | `README.md` | This index |
| L1 | `01-nivel-1-contexto.md` | C4 level 1: context, actors (5 roles + visitor) and external systems |
| L2 | `02-nivel-2-contenedores.md` | C4 level 2: containers (Next.js frontend, `nexusmarket-app`, MySQL) and Docker network |
| L3 | `03-nivel-3-componentes.md` | C4 level 3: internal components and request lifecycle |
| L4 | `04-nivel-4-codigo.md` | C4 level 4: key classes and collaborations |
| Layer | `05-capa-adapters-rest.md` | REST input adapter: controllers, DTOs, mappers, errors |
| Layer | `06-capa-adapters-usecases.md` | Use case adapters (implement the input ports) |
| Layer | `07-capa-adapters-persistence.md` | JPA persistence adapters over MySQL |
| Layer | `08-capa-adapters-security.md` | Security adapter: JWT, filter, `SecurityConfig`, BCrypt, principal |
| Layer | `09-capa-domain.md` | Pure domain: models, value objects, services, ports, exceptions |
| Layer | `10-capa-infrastructure.md` | Infrastructure: seeder, Docker, configuration, scripts |
| Flows | `11-endpoints-flujos.md` | Endpoint matrix (77 endpoints) and sequences per role |

## Layers and code

```text
nexusmarket/src/main/java/application/
|-- adapters/rest/          -> 05 (HTTP input)
|-- adapters/useCases/      -> 06 (implement domain/ports/in)
|-- adapters/persistence/   -> 07 (implement domain/ports/out against MySQL)
|-- adapters/security/      -> 08 (JWT, filter, config, BCrypt, principal)
|-- adapters/config/        -> 06 (DomainBeansConfig)
|-- domain/                 -> 09 (models, valueobjects, services, ports/in, ports/out, exceptions)
`-- infrastructure/seed/    -> 10 (DatabaseSeeder)
```

Cross-cutting rule: **dependencies point to the domain**.
`rest -> ports/in -> useCases -> services -> ports/out -> persistence/security`.
The domain never imports Spring, JPA, HTTP or JSON.

## Verified numbers

| Item | Value |
|---|---|
| REST endpoints | 77 (2 auth + 1 public + 11 buyer + 11 seller + 17 logistics + 22 admin + 13 supervisor) |
| Domain services | 54 (+ 2 helpers: `OwnedProductLoader`, `InventoryOperationGuard`) |
| Models / value objects / enums | 17 / 3 / 12 |
| Input ports / output ports | 6 / 13 |
| JPA entities / tables | 12 / 17 |
| Unit tests | 174 |
| End-to-end smoke checks | 46 (script `scripts/smoke-test.ps1`) |

## Diagram conventions

- `C4Context`, `C4Container` and `C4Component` for L1 to L3.
- `sequenceDiagram` for the flow of each group of endpoints.
- `classDiagram` for L4 with only the classes that take part in the flows.
'@

$docs["01-nivel-1-contexto.md"] = @'
# C4 Level 1 - System Context

NexusMarket is a marketplace where sellers publish products, buyers purchase them and the marketplace operation (logistics, administration, supervision) keeps stock, shipments, returns and refunds under control.

## Actors

| Actor | Role in the system | Spring authority |
|---|---|---|
| Visitor | Browses the published catalog and registers as a buyer | none |
| Buyer | Manages cart, orders, payment, returns and addresses | `ROLE_BUYER` |
| Seller | Manages own products, variants and inventory | `ROLE_SELLER` |
| Logistics operator | Prepares, packs, dispatches and delivers shipments; receives returns; adjusts inventory | `ROLE_LOGISTICS_OPERATOR` |
| Administrator | Registers sellers and internal users, changes statuses, approves or rejects returns, processes refunds, reads every report | `ROLE_ADMINISTRATOR` |
| Supervisor | Read-only access to consultations and reports | `ROLE_SUPERVISOR` |

Business rule RG-02: every user has exactly one role.

## External systems

| System | Status |
|---|---|
| Payment gateway | Not integrated. Payment approval is received as a boolean (`paymentApproved`) in `POST /buyer/orders/{orderId}/pay` |
| Carriers | Not integrated. Carrier name and tracking number are recorded by the logistics operator |
| MongoDB audit | Pending a decision of the professor (not part of the current build) |

```mermaid
C4Context
    title NexusMarket - System Context
    Person(visitor, "Visitor", "Browses the catalog and registers as buyer")
    Person(buyer, "Buyer", "Buys products, pays, requests returns")
    Person(seller, "Seller", "Publishes products and manages stock")
    Person(logistics, "Logistics operator", "Ships orders and receives returns")
    Person(admin, "Administrator", "Manages users, returns, refunds and reports")
    Person(supervisor, "Supervisor", "Read-only supervision")
    System(nexus, "NexusMarket", "Marketplace REST API with JWT security")
    System_Ext(web, "NexusMarket Web", "Next.js single page application")
    Rel(visitor, web, "Uses")
    Rel(buyer, web, "Uses")
    Rel(seller, web, "Uses")
    Rel(logistics, web, "Uses")
    Rel(admin, web, "Uses")
    Rel(supervisor, web, "Uses")
    Rel(web, nexus, "Calls /api/v1/**", "JSON over HTTP")
```
'@

$docs["02-nivel-2-contenedores.md"] = @'
# C4 Level 2 - Containers

## Containers

| Container | Technology | Responsibility |
|---|---|---|
| Web frontend | Next.js (to be built) | Role based screens; stores the JWT and sends it in every call |
| `nexusmarket-app` | Spring Boot 4.1.0, Java 17, Maven | REST API on port 8080: security, use cases, domain and persistence |
| `mysql-db` | MySQL 8 | Schema `nexusmarket_db` with 17 tables, port 3306 |

```mermaid
C4Container
    title NexusMarket - Containers
    Person(user, "Marketplace users", "Visitor, buyer, seller, logistics operator, administrator, supervisor")
    System_Boundary(sys, "NexusMarket") {
        Container(web, "Web frontend", "Next.js", "Role based screens")
        Container(api, "nexusmarket-app", "Spring Boot 4.1.0, Java 17", "REST API on port 8080")
        ContainerDb(db, "mysql-db", "MySQL 8", "nexusmarket_db, 17 tables, port 3306")
    }
    Rel(user, web, "Uses", "HTTPS")
    Rel(web, api, "Calls /api/v1/**", "JSON, Bearer JWT")
    Rel(api, db, "Reads and writes", "JDBC, Hibernate")
```

## Docker network

`docker-compose.yml` starts two services on the same network:

| Service | Image | Port | Notes |
|---|---|---|---|
| `mysql-db` | MySQL 8 | 3306 | Health check; the application waits until it is healthy |
| `nexusmarket-app` | Built from `nexusmarket/Dockerfile` (Maven + Temurin 17) | 8080 | Connects to `mysql-db`; a `maven_cache` volume speeds up rebuilds |

Start everything with `docker compose up -d --build`; stop with `docker compose down` (add `-v` to also erase the database).

## Environments

| Environment | Database | Configuration |
|---|---|---|
| Local / Docker | MySQL `nexusmarket_db` | `src/main/resources/application.properties` (`ddl-auto=update`) |
| Unit and integration tests | H2 in memory (MySQL mode, `create-drop`) | `src/test/resources/application.properties` |

JWT settings come from `jwt.secret` and `jwt.expiration-ms`; the allowed frontend origins from `FRONTEND_ORIGIN`.
'@

$docs["03-nivel-3-componentes.md"] = @'
# C4 Level 3 - Components of `nexusmarket-app`

```mermaid
C4Component
    title nexusmarket-app - Components
    Container_Boundary(api, "nexusmarket-app") {
        Component(sec, "Security", "Spring Security", "CORS, JwtAuthenticationFilter, SecurityConfig, role rules per route prefix")
        Component(rest, "REST adapters", "Spring MVC", "7 controllers, request and response DTOs, DomainRestMapper, GlobalExceptionHandler")
        Component(uc, "Use case adapters", "Spring beans with @Transactional", "6 UseCaseImpl, one per role port, delegation only")
        Component(dom, "Domain", "Plain Java", "17 models, 3 value objects, 12 enums, 54 services, 6 input ports, 13 output ports")
        Component(pers, "Persistence adapters", "Spring Data JPA", "11 adapters, 12 entities, 12 repositories, JPA mappers")
        Component(tok, "Token and password adapters", "JJWT, BCrypt", "JwtProvider, BCryptPasswordServiceAdapter")
        Component(seed, "Infrastructure", "CommandLineRunner", "DatabaseSeeder")
    }
    ContainerDb(db, "mysql-db", "MySQL 8", "nexusmarket_db")
    Rel(sec, rest, "Authenticated request")
    Rel(rest, uc, "Calls role port")
    Rel(uc, dom, "Delegates to domain services")
    Rel(dom, pers, "Through output ports")
    Rel(dom, tok, "Through output ports")
    Rel(pers, db, "JDBC")
    Rel(seed, dom, "Saves initial data through ports")
```

## Request lifecycle

```mermaid
sequenceDiagram
    participant SPA as Client
    participant F as CORS and JwtAuthenticationFilter
    participant S as SecurityFilterChain
    participant C as Controller
    participant U as UseCaseImpl (@Transactional)
    participant D as Domain service
    participant P as JPA adapter
    participant DB as MySQL
    SPA->>F: HTTP request with Bearer token
    F->>F: Validate token, reload user (LoadAuthenticatedUserService)
    F->>S: Authentication in the security context
    S->>S: Check the role for the route prefix (401 or 403)
    S->>C: Allowed request
    C->>U: Role port operation(actor, data)
    U->>D: Delegate to the service
    D->>D: requireRole, ownership and business rules
    D->>P: Output port (find, save)
    P->>DB: SQL
    DB-->>P: Rows
    P-->>D: Domain object
    D-->>U: Result
    U-->>C: Commit transaction
    C-->>SPA: DTO as JSON (or error from GlobalExceptionHandler)
```

## Two authorization layers

1. **Route level** (`SecurityConfig`): the prefix `/api/v1/<role>/**` requires the matching `ROLE_*`; failures answer 401 or 403 as JSON.
2. **Domain level**: every service calls `ValidateRoleAuthorizationService.requireRole` and, when needed, `ValidateOwnershipService.requireOwner`. A violation raises `UnauthorizedOperationException` (403).
'@

$docs["04-nivel-4-codigo.md"] = @'
# C4 Level 4 - Code

Only the classes that take part in the main flows are drawn.

## Purchase and billing

```mermaid
classDiagram
    class BuyerPort {
        <<interface>>
        +addItemToCart(actor, productId, quantity) Order
        +confirmOrder(actor, deliveryAddress) Order
        +payOrder(actor, orderId, paymentApproved) Order
    }
    class BuyerUseCaseImpl
    class PayOrderService {
        +pay(actor, orderId, paymentApproved) Order
    }
    class IssueInvoiceService {
        +issue(order, buyer) Invoice
    }
    class Order {
        +addItem(product, quantity)
        +confirm(address)
        +markPaid()
        +markShipped()
        +markDelivered()
        +total() Money
        +onlyDigital() boolean
    }
    class OrderItem
    class Invoice {
        +issueFor(order, buyer)$ Invoice
    }
    class OrderRepositoryPort {
        <<interface>>
    }
    class OrderJpaAdapter
    BuyerUseCaseImpl ..|> BuyerPort
    BuyerUseCaseImpl --> PayOrderService
    PayOrderService --> OrderRepositoryPort
    PayOrderService --> IssueInvoiceService
    PayOrderService ..> Order
    Order "1" *-- "many" OrderItem
    IssueInvoiceService ..> Invoice
    OrderJpaAdapter ..|> OrderRepositoryPort
```

## Shipping, returns and refunds

```mermaid
classDiagram
    class Shipment {
        +pack()
        +dispatch(carrier, trackingNumber)
        +markInTransit()
        +confirmDelivery()
    }
    class ReturnRequest {
        +approve()
        +reject(reason)
        +total() Money
    }
    class ReturnItem
    class Refund {
        +forReturn(returnRequest)$ Refund
    }
    class DispatchShipmentService
    class ConfirmDeliveryService
    class ReceiveReturnService
    class ProcessRefundService
    class RegisterInventorySaleService
    class RegisterInventoryReturnService
    ReturnRequest "1" *-- "many" ReturnItem
    DispatchShipmentService ..> Shipment
    DispatchShipmentService --> RegisterInventorySaleService
    ConfirmDeliveryService ..> Shipment
    ReceiveReturnService ..> ReturnRequest
    ReceiveReturnService --> RegisterInventoryReturnService
    ReceiveReturnService ..> Refund
    ProcessRefundService ..> Refund
```

## Authorization collaborators

```mermaid
classDiagram
    class ValidateRoleAuthorizationService {
        +requireRole(actor, roles...)
    }
    class ValidateOwnershipService {
        +requireOwner(actor, ownerUserId)
    }
    class AnyDomainService
    AnyDomainService --> ValidateRoleAuthorizationService
    AnyDomainService ..> ValidateOwnershipService : when the resource has an owner
```

Complete class tables are generated in `SDD/domain/Domain Model.md`, `SDD/domain/Domain Value Objects.md` and `SDD/domain/services/`.
'@

$docs["05-capa-adapters-rest.md"] = @'
# Layer - REST Adapters

Package `application/adapters/rest/`. Input adapter: it translates HTTP to calls on the input ports and domain objects to JSON. It contains no business rules.

| Component | Role |
|---|---|
| `controllers/` | 7 controllers: `AuthRestController`, `PublicRestController`, `BuyerRestController`, `SellerRestController`, `LogisticsRestController`, `AdminRestController`, `SupervisorRestController` |
| `dtos/requests/RequestDTOs` | Java `record` bodies with Jakarta Bean Validation |
| `dtos/responses/ResponseDTOs` | Java `record` responses; the domain objects and the password hash are never serialized |
| `mappers/DomainRestMapper` | Static methods that convert domain objects to response DTOs |
| `exception/GlobalExceptionHandler` | Translates exceptions to HTTP status and a uniform JSON body |

## Rules

- A controller depends only on one input port (for example `BuyerPort`).
- The authenticated user arrives with `@AuthenticationPrincipal(expression = "user") User actor` and is passed as the first argument of every port operation.
- Creation endpoints answer 201, password changes answer 204, everything else answers 200.
- Controllers never catch exceptions: `GlobalExceptionHandler` does.

## Error mapping

| Exception | HTTP |
|---|---|
| `InvalidCredentialsException` | 401 |
| `UnauthorizedOperationException` | 403 |
| `EntityNotFoundException`, unknown route | 404 |
| `InvalidDataException`, validation failures, malformed JSON, wrong parameter type | 400 |
| `HttpRequestMethodNotSupportedException` | 405 |
| Any other `DomainException` (stock, status transition, duplicate) | 409 |
| Anything else | 500 |

Body: `timestamp`, `status`, `error`, `message`, `path`, `requestId`.

Generated detail: `SDD/adapters/Api-rest-endpoints.md`, `Rest-adapters.md`, `Rest-validation.md` and `Global-exception-handler.md`.
'@

$docs["06-capa-adapters-usecases.md"] = @'
# Layer - Use Case Adapters

Package `application/adapters/useCases/`. One class per input port. A use case **only delegates** to domain services; it has no logic of its own.

| Class | Implements | Operations | Roles |
|---|---|---|---|
| `PublicAccessUseCaseImpl` | `PublicAccessPort` | 3 | none |
| `BuyerUseCaseImpl` | `BuyerPort` | 11 | `BUYER` |
| `SellerUseCaseImpl` | `SellerPort` | 11 | `SELLER` |
| `LogisticsOperatorUseCaseImpl` | `LogisticsOperatorPort` | 17 | `LOGISTICS_OPERATOR` |
| `AdministratorUseCaseImpl` | `AdministratorPort` | 22 | `ADMINISTRATOR` |
| `SupervisorUseCaseImpl` | `SupervisorPort` | 13 | `SUPERVISOR` |

Total: 77 operations, one per REST endpoint.

## Transactions

Every class is annotated with `@Transactional`. A multi-step operation is saved completely or rolled back completely. Example: paying an order reserves nothing new but issues the invoice and may deliver digital items; if any step fails the order stays unpaid.

## Wiring

Domain services are plain classes without Spring annotations. `adapters/config/DomainBeansConfig` registers them as beans with a component scan whose include filter is the regular expression `.*(Service|Guard|Loader|UseCaseImpl)$` and whose exclude filter is `.*(Fake|InMemory).*` (test doubles).

Generated detail: `SDD/adapters/Use-cases-adapters.md`.
'@

$docs["07-capa-adapters-persistence.md"] = @'
# Layer - Persistence Adapters

Package `application/adapters/persistence/jpa/`. Output adapters that implement the 13 output ports with Spring Data JPA over MySQL. The domain never sees JPA.

| Piece | Count | Notes |
|---|---|---|
| Adapters | 11 | `UserJpaAdapter`, `BuyerJpaAdapter`, `SellerJpaAdapter`, `WarehouseJpaAdapter`, `ProductJpaAdapter`, `InventoryJpaAdapter`, `OrderJpaAdapter`, `InvoiceJpaAdapter`, `ShipmentJpaAdapter`, `ReturnJpaAdapter`, `RefundJpaAdapter` |
| Entities | 12 | One per aggregate root |
| Spring Data repositories | 12 | `SpringDataJpa*Repository` |
| Mappers | static | `*JpaMapper`, domain <-> entity |

## Tables

| Table | Origin |
|---|---|
| `users`, `buyers`, `sellers`, `warehouses`, `products`, `inventories`, `inventory_movements`, `orders`, `invoices`, `shipments`, `return_requests`, `refunds` | 12 entities |
| `buyer_addresses`, `product_variants` | `@ElementCollection` |
| `order_items`, `invoice_lines`, `return_items` | `@Embeddable` rows (order items are reused by invoices) |

`inventories` has a unique constraint on product and warehouse.

## Mapping decisions

- Enumerations are stored as text with `name()` and rebuilt with `valueOf`.
- `Money` is stored as `BigDecimal` with precision 19 and scale 2.
- Lists are loaded eagerly (`FetchType.EAGER`) so the domain object is complete.
- After saving, an adapter returns the **same domain object** with its generated id assigned (`assignId`).
- Schema management: `spring.jpa.hibernate.ddl-auto=update`.

## Not yet implemented

MongoDB audit adapter: waiting for the professor's decision.

Generated detail: `SDD/adapters/Persistence-adapters.md` and `SDD/domain/Output-ports.md`.
'@

$docs["08-capa-adapters-security.md"] = @'
# Layer - Security Adapter

Package `application/adapters/security/`. Stateless security with JWT; there is no server session, therefore no logout service (the client discards the token).

| Class | Role |
|---|---|
| `SecurityConfig` | Filter chain: CSRF disabled, stateless sessions, JSON 401 and 403 handlers, route rules, CORS source, BCrypt bean |
| `JwtProvider` | Implements `JwtServicePort`: issues and validates HMAC signed tokens |
| `JwtAuthenticationFilter` | Validates the token, reloads the user and fills the security context |
| `AuthenticatedUserPrincipal` | `UserDetails` wrapper exposing the domain `User` and the authority `ROLE_<role>` |
| `BCryptPasswordServiceAdapter` | Implements `PasswordServicePort` (encode and matches) |

## Route rules

| Prefix | Access |
|---|---|
| `/api/v1/auth/**`, `/api/v1/public/**`, `/actuator/health`, `/actuator/info` | public |
| `/api/v1/buyer/**` | `BUYER` |
| `/api/v1/seller/**` | `SELLER` |
| `/api/v1/logistics/**` | `LOGISTICS_OPERATOR` |
| `/api/v1/admin/**` | `ADMINISTRATOR` |
| `/api/v1/supervisor/**` | `SUPERVISOR` |
| any other request | authenticated |

## Design decision: the user is reloaded on every request

Instead of rebuilding the user from the token claims, `JwtAuthenticationFilter` validates the token and loads the persisted user through `LoadAuthenticatedUserService`. A blocked or deactivated user therefore loses access immediately, even with a valid token. The cost is one primary key query per request.

## CORS

Allowed origins come from the property `FRONTEND_ORIGIN` (default `http://localhost:3000,http://127.0.0.1:3000,http://localhost:5173`); allowed methods GET, POST, PUT, PATCH, DELETE, OPTIONS; allowed headers `Authorization`, `Content-Type`, `X-Request-Id`.

## Passwords and the seed secret

Passwords are stored as BCrypt hashes. The JWT secret has a public academic default in `application.properties`; in a real deployment it must be supplied through the environment.

Generated detail: `SDD/adapters/Rest-security-cors.md`.
'@

$docs["09-capa-domain.md"] = @'
# Layer - Domain

Package `application/domain/`. Pure core: **zero dependencies** on Spring, JPA, HTTP or JSON. Everything external enters through output ports that the domain owns as interfaces.

| Package | Content |
|---|---|
| `models/` | 17 classes: `User`, `Buyer`, `Seller`, `Warehouse`, `Product`, `Inventory`, `InventoryMovement`, `Order`, `OrderItem`, `Invoice`, `Shipment`, `ReturnRequest`, `ReturnItem`, `Refund`, `SalesReport`, `AuthenticationResult`, `Guard` |
| `valueobjects/` | 3 value objects (`Money`, `Email`, `DocumentNumber`) and 12 enums (`UserRole`, `UserStatus`, `CommercialStatus`, `ProductType`, `ProductStatus`, `InventoryStatus`, `MovementType`, `OrderStatus`, `ShipmentStatus`, `ReturnStatus`, `RefundStatus`, `WarehouseType`) |
| `services/` | 54 services plus 2 helpers, grouped by subdomain: user, buyer, seller, catalog, warehouse, inventory, order, billing, shipping, returns, report, authorization |
| `ports/in/` | 6 input ports, one per role |
| `ports/out/` | 13 output ports: 11 repositories, `PasswordServicePort`, `JwtServicePort` |
| `exceptions/` | `DomainException` and its subclasses |

## Exceptions

| Exception | Meaning | HTTP |
|---|---|---|
| `InvalidCredentialsException` | Wrong credentials or invalid token | 401 |
| `UnauthorizedOperationException` | Role or ownership violation | 403 |
| `EntityNotFoundException` | Entity does not exist | 404 |
| `InvalidDataException` | Invalid input data | 400 |
| `InvalidStatusTransitionException`, `DuplicateEntityException`, other `DomainException` | Business rule violated | 409 |

## Design rules

- Entities protect their invariants: state changes only through methods (`markPaid`, `pack`, `approve`) that reject invalid transitions.
- Value objects are immutable and validated on creation.
- Every service receives the acting `User` first and calls `ValidateRoleAuthorizationService.requireRole`.
- Services are plain classes; the adapter layer registers them as beans.
- Internal services (`ReserveInventoryService`, `RegisterInventorySaleService`, `RegisterInventoryReturnService`, `IssueInvoiceService`) have no role port: other services invoke them.
- Two helpers remove duplicated validation: `OwnedProductLoader` (catalog) and `InventoryOperationGuard` (inventory).

Generated detail: `SDD/domain/Domain Model.md`, `Domain Value Objects.md`, `Domain Services.md`, `services/*.md`, `Input-ports.md` and `Output-ports.md`.
'@

$docs["10-capa-infrastructure.md"] = @'
# Layer - Infrastructure

## DatabaseSeeder

`application/infrastructure/seed/DatabaseSeeder.java`, a `CommandLineRunner`. It is idempotent: if `admin@nexusmarket.com` already exists it logs `Seeds already loaded, skipping.` and does nothing.

| Seeded data | Detail |
|---|---|
| Users (password `Password123*`) | `admin@`, `supervisor@`, `logistica@`, `comprador@` and `vendedor@nexusmarket.com` (one per role) |
| Buyer / seller | 1 buyer profile; 1 seller, trade name TechStore SAS |
| Warehouses | 2 |
| Products | Audifonos Bluetooth (150000), Teclado mecanico (320000), Curso de Java (digital, 89000); all published |
| Stock | Inventory for the two physical products |

## Configuration

| File | Purpose |
|---|---|
| `src/main/resources/application.properties` | MySQL datasource, JPA, JWT (`jwt.secret`, `jwt.expiration-ms`), port 8080 |
| `src/test/resources/application.properties` | H2 in memory so tests need no MySQL |
| `docker-compose.yml`, `nexusmarket/Dockerfile` | MySQL and application containers |

## Quality tooling

| Tool | Purpose |
|---|---|
| `.\mvnw.cmd test` | 174 unit tests (domain, services, use cases, mappers, controllers, JWT, persistence) |
| `scripts/smoke-test.ps1` | 46 end-to-end checks against the running API: role security, validation, purchase, shipment, return and refund, seller and administrator operations |
| `scripts/generate-sdd-*.ps1` | Generate the SDD documents from the source code so documentation and code agree |
'@

$docs["11-endpoints-flujos.md"] = @'
# Endpoint Flows

Matrix verified against `adapters/rest/controllers/*.java` (77 endpoints). The complete list with request bodies and status codes is generated in `SDD/adapters/Api-rest-endpoints.md`. Paths are relative to `/api/v1`.

## 1. Public access - `AuthRestController`, `PublicRestController`

| Verb | Path | Service | Notes |
|---|---|---|---|
| POST | `/auth/login` | `LoginService` | Returns a signed JWT; 401 on wrong credentials |
| POST | `/auth/register/buyer` | `RegisterBuyerService` | 201; 409 if the e-mail or document already exists |
| GET | `/public/products` | `ConsultPublishedProductsService` | Only published products |

## 2. Buyer - `BuyerRestController` (`BuyerPort`, `ROLE_BUYER`)

| Verb | Path | Service | Notes |
|---|---|---|---|
| POST | `/buyer/addresses` | `AddBuyerAddressService` | 201 |
| POST | `/buyer/cart/items` | `AddItemToCartService` | 201; creates the cart if needed |
| DELETE | `/buyer/cart/items/{productId}` | `RemoveItemFromCartService` | |
| POST | `/buyer/orders/confirm` | `ConfirmOrderService` | The address must be a registered address; reserves stock |
| POST | `/buyer/orders/{orderId}/pay` | `PayOrderService` | Issues the invoice; digital products are delivered at once |
| GET | `/buyer/orders` | `ConsultMyOrdersService` | |
| GET | `/buyer/orders/{orderId}/invoice` | `ConsultInvoiceService` | |
| GET | `/buyer/orders/{orderId}/shipment` | `ConsultShipmentService` | |
| POST | `/buyer/returns` | `RequestReturnService` | 201 |
| GET | `/buyer/returns` | `ConsultMyReturnsService` | |
| PUT | `/buyer/password` | `ChangeUserPasswordService` | 204 |

```mermaid
sequenceDiagram
    participant B as Buyer
    participant C as BuyerRestController
    participant U as BuyerUseCaseImpl
    participant S as Order services
    participant I as Inventory and invoice services
    participant R as JPA adapters
    B->>C: POST /buyer/cart/items
    C->>U: addItemToCart
    U->>S: AddItemToCartService
    S->>R: save cart
    B->>C: POST /buyer/orders/confirm
    C->>U: confirmOrder
    U->>S: ConfirmOrderService
    S->>I: ReserveInventoryService
    I->>R: save reservation movement
    B->>C: POST /buyer/orders/{id}/pay
    C->>U: payOrder
    U->>S: PayOrderService
    S->>I: IssueInvoiceService
    I->>R: save invoice
    S->>R: save order PAID
    C-->>B: 200 order
```

## 3. Seller - `SellerRestController` (`SellerPort`, `ROLE_SELLER`)

| Verb | Path | Service |
|---|---|---|
| POST | `/seller/products` | `RegisterProductService` |
| POST | `/seller/products/{id}/publish` | `PublishProductService` |
| POST | `/seller/products/{id}/variants` | `AddProductVariantService` |
| POST | `/seller/products/{id}/suspend` | `SuspendProductService` |
| POST | `/seller/products/{id}/discontinue` | `DiscontinueProductService` |
| POST | `/seller/inventory/entries` | `RegisterInventoryEntryService` |
| PUT | `/seller/inventory/adjustment` | `AdjustInventoryService` |
| PUT | `/seller/inventory/status` | `ChangeInventoryStatusService` |
| GET | `/seller/inventory/product/{productId}` | `ConsultInventoryService` |
| GET | `/seller/warehouses` | `ConsultWarehousesService` |
| PUT | `/seller/password` | `ChangeUserPasswordService` |

A seller can only operate on its own products and warehouses (`OwnedProductLoader`, `InventoryOperationGuard`).

## 4. Logistics operator - `LogisticsRestController` (`LogisticsOperatorPort`, `ROLE_LOGISTICS_OPERATOR`)

| Verb | Path | Service |
|---|---|---|
| POST | `/logistics/orders/{orderId}/shipment/prepare` | `StartShipmentPreparationService` |
| POST | `/logistics/shipments/{id}/pack` | `PackShipmentService` |
| POST | `/logistics/shipments/{id}/dispatch` | `DispatchShipmentService` |
| POST | `/logistics/shipments/{id}/in-transit` | `RegisterShipmentInTransitService` |
| POST | `/logistics/shipments/{id}/deliver` | `ConfirmDeliveryService` |
| POST | `/logistics/returns/{id}/receive` | `ReceiveReturnService` |
| POST, PUT, GET | `/logistics/inventory/...` (entries, adjustment, status, product, movements) | inventory services |
| GET | `/logistics/warehouses`, `/orders`, `/shipments`, `/orders/{id}/shipment`, `/returns` | consult services |
| PUT | `/logistics/password` | `ChangeUserPasswordService` |

```mermaid
sequenceDiagram
    participant L as Logistics operator
    participant U as LogisticsOperatorUseCaseImpl
    participant S as Shipping services
    participant I as RegisterInventorySaleService
    participant R as JPA adapters
    L->>U: startShipmentPreparation(orderId)
    U->>S: order must be PAID and have physical items
    S->>R: save shipment PREPARING
    L->>U: packShipment
    L->>U: dispatchShipment(carrier, tracking)
    U->>S: DispatchShipmentService
    S->>I: register stock exit
    S->>R: order SHIPPED
    L->>U: registerShipmentInTransit
    L->>U: confirmDelivery
    U->>S: ConfirmDeliveryService
    S->>R: shipment and order DELIVERED
```

## 5. Administrator - `AdminRestController` (`AdministratorPort`, `ROLE_ADMINISTRATOR`)

| Verb | Path | Service |
|---|---|---|
| POST | `/admin/sellers` | `RegisterSellerService` |
| POST | `/admin/users` | `RegisterInternalUserService` |
| PUT | `/admin/users/{id}/status` | `ChangeUserStatusService` |
| PUT | `/admin/buyers/{id}/commercial-status` | `ChangeBuyerCommercialStatusService` |
| POST | `/admin/warehouses` | `CreateWarehouseService` |
| PUT | `/admin/warehouses/{id}/status` | `ChangeWarehouseStatusService` |
| POST | `/admin/returns/{id}/approve` | `ApproveReturnService` |
| POST | `/admin/returns/{id}/reject` | `RejectReturnService` |
| POST | `/admin/refunds/{id}/process` | `ProcessRefundService` |
| GET | `/admin/users`, `/warehouses`, `/orders`, `/invoices`, `/shipments`, `/returns`, `/refunds` | consult services |
| GET | `/admin/inventory/product/{id}`, `/inventory/movements`, `/orders/{id}/invoice`, `/orders/{id}/shipment` | consult services |
| GET | `/admin/reports/sales` | `GenerateSalesReportService` |
| PUT | `/admin/password` | `ChangeUserPasswordService` |

```mermaid
sequenceDiagram
    participant B as Buyer
    participant A as Administrator
    participant L as Logistics operator
    participant S as Return services
    participant R as JPA adapters
    B->>S: POST /buyer/returns (RequestReturnService)
    S->>R: save return REQUESTED
    A->>S: POST /admin/returns/{id}/approve
    S->>R: return APPROVED
    L->>S: POST /logistics/returns/{id}/receive
    S->>R: restock items and create pending refund
    A->>S: POST /admin/refunds/{id}/process
    S->>R: refund PROCESSED (only once)
```

## 6. Supervisor - `SupervisorRestController` (`SupervisorPort`, `ROLE_SUPERVISOR`)

13 read-only endpoints: the same consultations and the sales report of the administrator (`GET /supervisor/users`, `/warehouses`, `/inventory/product/{id}`, `/inventory/movements`, `/orders`, `/invoices`, `/orders/{id}/invoice`, `/shipments`, `/orders/{id}/shipment`, `/returns`, `/refunds`, `/reports/sales`) plus `PUT /supervisor/password`.

## 7. Error table

| Situation | HTTP |
|---|---|
| No token, invalid token or wrong credentials | 401 |
| Role not allowed for the route or the operation | 403 |
| Entity or route not found | 404 |
| Invalid body, missing field or wrong parameter type | 400 |
| HTTP verb not supported for the route | 405 |
| Insufficient stock, invalid status transition, duplicate data, unregistered address | 409 |
'@

foreach ($name in $docs.Keys) {
    $content = ($docs[$name] -replace "`r`n", "`n")
    [System.IO.File]::WriteAllText((Join-Path $dir $name), $content, $utf8)
    Write-Host ("OK  {0}" -f $name)
}
Write-Host ("`nGenerado en {0}: {1} archivos" -f $dir, $docs.Count) -ForegroundColor Green