# Shipping Services

Package `application.domain.services.shipping`. 7 classes. Generated from the source code.

## Confirm Delivery

Confirms the delivery of a shipment and marks the order as delivered.

- **Class:** `application.domain.services.shipping.ConfirmDeliveryService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`
- **Operation:** `Shipment confirm(User actor, Long shipmentId)`

**Collaborators (injected through the constructor):**

- `ShipmentRepositoryPort`
- `OrderRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Envio no encontrado. / Pedido no encontrado. |

**Covered by tests:** `ShippingServicesTest`

---

## Consult Shipment

Retrieves the shipment of an order for its owner or for a back-office role.

- **Class:** `application.domain.services.shipping.ConsultShipmentService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `Shipment consultShipment(User actor, Long orderId)`

**Collaborators (injected through the constructor):**

- `ShipmentRepositoryPort`
- `OrderRepositoryPort`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Orden no encontrada. / El pedido aún no se ha enviado.. |

**Covered by tests:** `ShipmentQueryServicesTest`

---

## Consult Shipments

Lists every shipment for back-office roles.

- **Class:** `application.domain.services.shipping.ConsultShipmentsService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `List<Shipment> consultShipments(User actor)`

**Collaborators (injected through the constructor):**

- `ShipmentRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `ShipmentQueryServicesTest`

---

## Dispatch Shipment

Dispatches a packed shipment with its carrier and tracking number, marks the order as shipped and records the inventory sale.

- **Class:** `application.domain.services.shipping.DispatchShipmentService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`
- **Operation:** `Shipment dispatch(User actor, Long shipmentId, String carrier, String trackingNumber)`

**Collaborators (injected through the constructor):**

- `ShipmentRepositoryPort`
- `OrderRepositoryPort`
- `RegisterInventorySaleService`
- `ValidateRoleAuthorizationService`

**Business rules and notes (from the code):**

- valida estado y datos

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Envio no encontrado. / Pedido no encontrado. |

**Covered by tests:** `UseCaseImplDelegationTest`, `ShippingServicesTest`

---

## Pack Shipment

Marks a shipment as packed and ready to be dispatched.

- **Class:** `application.domain.services.shipping.PackShipmentService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`
- **Operation:** `Shipment pack(User actor, Long shipmentId)`

**Collaborators (injected through the constructor):**

- `ShipmentRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado el envío. |

**Covered by tests:** `ShipmentProgressServicesTest`

---

## Register Shipment In Transit

Marks a dispatched shipment as in transit.

- **Class:** `application.domain.services.shipping.RegisterShipmentInTransitService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`
- **Operation:** `Shipment register(User actor, Long shipmentId)`

**Collaborators (injected through the constructor):**

- `ShipmentRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado el envío. |

**Covered by tests:** `ShipmentProgressServicesTest`

---

## Start Shipment Preparation

Creates the shipment of a paid order and starts its preparation.

- **Class:** `application.domain.services.shipping.StartShipmentPreparationService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `LOGISTICS_OPERATOR`
- **Operation:** `Shipment start(User actor, Long orderId)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `ShipmentRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado el pedido. |
| `InvalidStatusTransitionException` | 409 | Solo se puede tramitar un pedido pagado. Estado actual:  |
| `InvalidDataException` | 400 | Los pedidos exclusivamente digitales no requieren gastos de envío. |
| `DuplicateEntityException` | 409 | El pedido ya se ha enviado. |

**Covered by tests:** `ShippingServicesTest`

---
