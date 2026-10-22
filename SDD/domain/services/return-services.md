# Return and Refund Services

Package `application.domain.services.returns`. 8 classes. Generated from the source code.

## Approve Return

Approves a pending return request so that the buyer can send the items back.

- **Class:** `application.domain.services.returns.ApproveReturnService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `ReturnRequest approve(User actor, Long returnId)`

**Collaborators (injected through the constructor):**

- `ReturnRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado ningún resultado. |

**Covered by tests:** `ReturnFulfillmentServicesTest`

---

## Consult My Returns

Lists the return requests of the authenticated buyer.

- **Class:** `application.domain.services.returns.ConsultMyReturnsService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `List<ReturnRequest> consultMyReturns(User actor)`

**Collaborators (injected through the constructor):**

- `ReturnRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `ReturnQueryServicesTest`

---

## Consult Refunds

Lists every refund for back-office roles.

- **Class:** `application.domain.services.returns.ConsultRefundsService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `List<Refund> consultRefunds(User actor)`

**Collaborators (injected through the constructor):**

- `RefundRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `ReturnQueryServicesTest`

---

## Consult Returns

Lists every return request for back-office roles.

- **Class:** `application.domain.services.returns.ConsultReturnsService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`
- **Operation:** `List<ReturnRequest> consultReturns(User actor)`

**Collaborators (injected through the constructor):**

- `ReturnRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `ReturnQueryServicesTest`

---

## Process Refund

Processes a pending refund. A refund can only be processed once.

- **Class:** `application.domain.services.returns.ProcessRefundService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `Refund process(User actor, Long refundId)`

**Collaborators (injected through the constructor):**

- `RefundRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se encontro resultado. |

**Covered by tests:** `ReturnFulfillmentServicesTest`

---

## Receive Return

Registers the physical reception of an approved return, returns the stock to inventory and creates the pending refund.

- **Class:** `application.domain.services.returns.ReceiveReturnService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`
- **Operation:** `ReturnRequest receive(User actor, Long returnId)`

**Collaborators (injected through the constructor):**

- `ReturnRepositoryPort`
- `RefundRepositoryPort`
- `RegisterInventoryReturnService`
- `ValidateRoleAuthorizationService`

**Business rules and notes (from the code):**

- lanza error si no estaba aprobada

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se encontro resultado. |

**Covered by tests:** `ReturnFulfillmentServicesTest`

---

## Reject Return

Rejects a return request recording the rejection reason.

- **Class:** `application.domain.services.returns.RejectReturnService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `ReturnRequest reject(User actor, Long returnId, String reason)`

**Collaborators (injected through the constructor):**

- `ReturnRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Resultado no encontrado. |

**Covered by tests:** `RejectReturnServiceTest`

---

## Request Return

Creates a return request for delivered items of an order of the authenticated buyer.

- **Class:** `application.domain.services.returns.RequestReturnService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `ReturnRequest request(User actor, RequestReturnCommand command)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `ReturnRepositoryPort`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Business rules and notes (from the code):**

- Unidades de ese producto ya devueltas o en trámite (las rechazadas no cuentan)

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Pedido no encontrado. |
| `InvalidStatusTransitionException` | 409 | Solo se pueden devolver los pedidos que ya se hayan entregado. |
| `InvalidDataException` | 400 | Se debe devolver al menos un producto. / Solo  / The product  |

**Covered by tests:** `RequestReturnServiceTest`

---
