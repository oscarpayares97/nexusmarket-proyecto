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