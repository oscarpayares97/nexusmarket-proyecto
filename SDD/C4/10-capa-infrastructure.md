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