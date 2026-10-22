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