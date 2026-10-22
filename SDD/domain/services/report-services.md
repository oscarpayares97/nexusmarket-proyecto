# Reporting Services

Package `application.domain.services.report`. 1 classes. Generated from the source code.

## Generate Sales Report

Builds the sales report: gross sales, refunded amount, net sales, orders by status and sales by seller.

- **Class:** `application.domain.services.report.GenerateSalesReportService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `SalesReport generate(User actor)`

**Collaborators (injected through the constructor):**

- `InvoiceRepositoryPort`
- `RefundRepositoryPort`
- `OrderRepositoryPort`
- `ProductRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Producto no encontrado. |

**Covered by tests:** `UseCaseImplDelegationTest`, `GenerateSalesReportServiceTest`

---
