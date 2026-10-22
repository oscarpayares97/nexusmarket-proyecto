# NexusMarket

Marketplace backend built with **Spring Boot**, **Java 17**, **hexagonal architecture** and **Domain-Driven Design**. Sellers publish products, buyers purchase and pay, logistics operators ship and receive returns, and administrators and supervisors manage and observe the marketplace.

## Architecture

```text
rest controllers -> input ports -> use cases -> domain services -> output ports -> persistence / security adapters
```

- `domain/`: models, value objects, enums, services, ports and exceptions. No Spring, JPA or HTTP dependencies.
- `adapters/rest/`: controllers, DTOs, mappers and the global exception handler.
- `adapters/useCases/`: one transactional class per role port; it only delegates to domain services.
- `adapters/persistence/`: Spring Data JPA adapters over MySQL.
- `adapters/security/`: stateless JWT, BCrypt, role rules per route, CORS.
- `infrastructure/`: database seeder.

| Item | Value |
|---|---|
| Roles | Buyer, Seller, Logistics operator, Administrator, Supervisor (plus public access) |
| REST endpoints | 77 |
| Domain services | 54 |
| JPA entities / tables | 12 / 17 |
| Unit tests | 174 |
| Smoke checks | 46 |

## Requirements

- Docker Desktop (with Compose)
- To run the tests locally: JDK 17 (the Maven wrapper is included)
- PowerShell for the helper scripts (Windows)

## Run with Docker

```powershell
docker compose up -d --build
docker compose logs -f nexusmarket-app
```

The first start downloads Maven dependencies and can take several minutes. Wait for `Started NexusmarketApplication`. The API listens on `http://localhost:8080` and MySQL on port 3306.

```powershell
docker compose down        # stop, keep the data
docker compose down -v     # stop and erase the database (the seeder recreates the data)
```

Health check: `GET http://localhost:8080/actuator/health`

## Test users

The seeder creates one user per role. All use the password `Password123*`.

| Role | E-mail |
|---|---|
| Administrator | admin@nexusmarket.com |
| Supervisor | supervisor@nexusmarket.com |
| Logistics operator | logistica@nexusmarket.com |
| Buyer | comprador@nexusmarket.com |
| Seller | vendedor@nexusmarket.com |

Log in with `POST /api/v1/auth/login` and send the returned token as `Authorization: Bearer <token>`.

## API overview

Base path `/api/v1`.

| Prefix | Access |
|---|---|
| `/auth/**`, `/public/**` | public |
| `/buyer/**` | `BUYER` |
| `/seller/**` | `SELLER` |
| `/logistics/**` | `LOGISTICS_OPERATOR` |
| `/admin/**` | `ADMINISTRATOR` |
| `/supervisor/**` | `SUPERVISOR` |

Errors use a uniform JSON body: `timestamp`, `status`, `error`, `message`, `path`, `requestId`.
The full endpoint list is in `SDD/adapters/Api-rest-endpoints.md`.

## Tests

```powershell
cd nexusmarket
.\mvnw.cmd test
```

Runs 174 unit tests (domain, services, use cases, mappers, controllers, JWT). Tests use H2 in memory, so MySQL is not needed.

End-to-end smoke test against the running API (46 checks: role security, validation, purchase, shipment, return and refund, seller and administrator operations):

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
```

## Documentation (SDD)

| Folder | Content |
|---|---|
| `SDD/domain/` | Domain model, value objects, services, input and output ports |
| `SDD/adapters/` | REST endpoints, use case adapters, persistence, validation, errors, security |
| `SDD/C4/` | C4 model (4 levels), one sheet per layer and endpoint flows |
| `SDD/Software Architecture/` | Architecture overview |

Most SDD documents are generated from the source code so that documentation and code stay consistent:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-services.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-domain.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\generate-sdd-adapters.ps1
```

## Front end

The web client (Next.js) lives in `frontend/`:

```powershell
cd frontend
npm install
npm run dev
```

It runs on `http://localhost:3000`. The API URL is set in `frontend/.env.local` (`NEXT_PUBLIC_API_URL`). The backend allows this origin through the `FRONTEND_ORIGIN` property.

## Pending

- MongoDB audit adapter (waiting for the professor's decision).