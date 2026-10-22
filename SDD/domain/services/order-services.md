# Order Management Services

Package `application.domain.services.order`. 6 classes. Generated from the source code.

## Add Item To Cart

Adds a product to the open cart of the authenticated buyer, creating the cart if it does not exist.

- **Class:** `application.domain.services.order.AddItemToCartService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `Order add(User actor, Long productId, int quantity)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `ProductRepositoryPort`
- `ValidateRoleAuthorizationService`

**Business rules and notes (from the code):**

- Cada comprador tiene un carrito abierto: se reutiliza o se crea

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Producto no encontrado. |
| `InvalidDataException` | 400 | El producto no esta publicado. |

**Covered by tests:** `UseCaseImplDelegationTest`, `AddItemToCartServiceTest`

---

## Confirm Order

Confirms the cart as an order with a delivery address and reserves the stock of its items.

- **Class:** `application.domain.services.order.ConfirmOrderService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `Order confirm(User actor, String deliveryAddress)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `BuyerRepositoryPort`
- `ProductRepositoryPort`
- `ReserveInventoryService`
- `ValidateRoleAuthorizationService`

**Business rules and notes (from the code):**

- 1) Validar todo antes de reservar
- 2) Reservar el inventario de los productos físicos
- 3) Confirmar el pedido

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado ningún comprador. / Producto no encontrado. |
| `UnauthorizedOperationException` | 403 | El comprador ha sido suspendido y no puede realizar pedidos. |
| `InvalidDataException` | 400 | La dirección de entrega debe ser una de las direcciones registradas del comprador. / El carrito está vacío. / El producto ' |
| `InsufficientStockException` | 409 | Existencias insuficientes de ' |

**Covered by tests:** `ConfirmOrderServiceTest`

---

## Consult My Orders

Lists the orders of the authenticated buyer.

- **Class:** `application.domain.services.order.ConsultMyOrdersService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `List<Order> consultMyOrders(User actor)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `OrderQueryServicesTest`

---

## Consult Orders

Lists every order for back-office roles.

- **Class:** `application.domain.services.order.ConsultOrdersService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`
- **Operation:** `List<Order> consultOrders(User actor)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `OrderQueryServicesTest`

---

## Pay Order

Registers the payment of an order, issues its invoice and delivers digital products immediately.

- **Class:** `application.domain.services.order.PayOrderService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `Order pay(User actor, Long orderId, boolean paymentApproved)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `IssueInvoiceService`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Business rules and notes (from the code):**

- si el estado no corresponde, lanza el error adecuado
- Entrega inmediata de productos digitales

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado el pedido. |
| `InvalidDataException` | 400 | El pago ha sido rechazado. El pedido sigue pendiente de pago.. |

**Covered by tests:** `PayOrderServiceTest`

---

## Remove Item From Cart

Removes a product from the open cart of the authenticated buyer.

- **Class:** `application.domain.services.order.RemoveItemFromCartService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `Order remove(User actor, Long productId)`

**Collaborators (injected through the constructor):**

- `OrderRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No hay ningún carrito abierto. |

**Covered by tests:** `UseCaseImplDelegationTest`, `OrderQueryServicesTest`

---
