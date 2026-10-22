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