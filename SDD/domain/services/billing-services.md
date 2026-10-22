# Billing Services

Package `application.domain.services.billing`. 3 classes. Generated from the source code.

## Consult Invoice

Retrieves the invoice of an order for its owner or for a back-office role.

- **Class:** `application.domain.services.billing.ConsultInvoiceService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`, `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `Invoice consultInvoice(User actor, Long orderId)`

**Collaborators (injected through the constructor):**

- `InvoiceRepositoryPort`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | El pedido no tiene factura. |

**Covered by tests:** `InvoiceQueryServicesTest`

---

## Consult Invoices

Lists every issued invoice for back-office roles.

- **Class:** `application.domain.services.billing.ConsultInvoicesService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `List<Invoice> consultInvoices(User actor)`

**Collaborators (injected through the constructor):**

- `InvoiceRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `InvoiceQueryServicesTest`

---

## Issue Invoice

Issues the invoice of a paid order. An order can only be invoiced once. Internal service.

- **Class:** `application.domain.services.billing.IssueInvoiceService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Invoice issue(Order order, User buyer)`

**Collaborators (injected through the constructor):**

- `InvoiceRepositoryPort`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `DuplicateEntityException` | 409 | Tu orden esta lista para ser facturada. |

**Covered by tests:** `IssueInvoiceServiceTest`, `PayOrderServiceTest`

---
