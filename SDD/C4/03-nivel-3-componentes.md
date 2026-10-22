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