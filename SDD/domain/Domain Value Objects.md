# Domain Value Objects

Package `application.domain.valueobjects`. Immutable value objects and enumerations, generated from the source code.

---

# Value objects

## DocumentNumber

Validated identity document number.

- **Component:** `String value`

**Validations:**

- `InvalidDataException: El documento es requerido.`
- `InvalidDataException: El documento debe contener como máximo 20 letras, números o guiones.`

---

## Email

Validated e-mail address.

- **Component:** `String value`

**Validations:**

- `InvalidDataException: El email es requerido.`
- `InvalidDataException: El email no tiene un formato valido.`

---

## Money

Monetary amount with exact decimal arithmetic.

- **Component:** `BigDecimal amount`
- **Operation:** `static Money of(String value)`
- **Operation:** `Money add(Money other)`
- **Operation:** `Money subtract(Money other)`
- **Operation:** `Money multiply(int quantity)`
- **Operation:** `boolean isGreaterThanZero()`

**Validations:**

- `InvalidDataException: Es obligatorio indicar el importe.`
- `InvalidDataException: La cantidad no puede ser negativa.`

---

# Enumerations

| Enum | Values | Purpose |
|---|---|---|
| `CommercialStatus` | `ENABLED`, `SUSPENDED` | Whether a buyer is allowed to purchase. |
| `InventoryStatus` | `AVAILABLE`, `DAMAGED` | Operational status of an inventory record. |
| `MovementType` | `INFLOW`, `RESERVATION`, `SALE`, `ADJUSTMENT`, `RETURN` | Kind of inventory movement. |
| `OrderStatus` | `CART`, `PENDING_PAYMENT`, `PAID`, `SHIPPED`, `DELIVERED` | Lifecycle of an order, from cart to delivered. |
| `ProductStatus` | `PUBLISHED`, `SUSPENDED`, `DISCONTINUED` | Publication lifecycle of a product. |
| `ProductType` | `PHYSICAL`, `DIGITAL` | Physical products manage inventory and shipping; digital products are delivered immediately. |
| `RefundStatus` | `PENDING`, `PROCESSED` | Lifecycle of a refund. |
| `ReturnStatus` | `REQUESTED`, `APPROVED`, `REJECTED`, `RECEIVED` | Lifecycle of a return request. |
| `ShipmentStatus` | `PREPARING`, `PACKED`, `SHIPPED`, `IN_TRANSIT`, `DELIVERED` | Lifecycle of a shipment. |
| `UserRole` | `BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR` | Role of a user in the marketplace (RG-02: one role per user). |
| `UserStatus` | `ACTIVE`, `BLOCKED`, `INACTIVE` | Access status of a user; only ACTIVE users can operate. |
| `WarehouseType` | `MARKETPLACE`, `SELLER` | Whether a warehouse belongs to the marketplace or to a seller. |
