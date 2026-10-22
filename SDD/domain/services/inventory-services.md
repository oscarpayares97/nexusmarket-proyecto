# Inventory Management Services

Package `application.domain.services.inventory`. 9 classes. Generated from the source code.

## Adjust Inventory

Sets the available quantity of a product in a warehouse to a corrected value and records the adjustment movement.

- **Class:** `application.domain.services.inventory.AdjustInventoryService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Inventory adjust(User actor, Long productId, Long warehouseId, int newQuantity)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`
- `InventoryOperationGuard`

**Covered by tests:** `UseCaseImplDelegationTest`, `InventoryManagementServicesTest`

---

## Change Inventory Status

Changes the operational status of an inventory record.

- **Class:** `application.domain.services.inventory.ChangeInventoryStatusService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Inventory change(User actor, Long productId, Long warehouseId, InventoryStatus status)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`
- `InventoryOperationGuard`

**Covered by tests:** `InventoryManagementServicesTest`

---

## Consult Inventory Movements

Lists the inventory movements (entries, reservations, sales, adjustments, returns).

- **Class:** `application.domain.services.inventory.ConsultInventoryMovementsService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`
- **Operation:** `List<InventoryMovement> consultMovements(User actor)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `InventoryQueryServicesTest`

---

## Consult Inventory

Retrieves the stock of a product across warehouses.

- **Class:** `application.domain.services.inventory.ConsultInventoryService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `List<Inventory> consultInventory(User actor, Long productId)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`
- `ProductRepositoryPort`
- `ValidateRoleAuthorizationService`
- `ValidateOwnershipService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Producto no encontrado. |

**Covered by tests:** `UseCaseImplDelegationTest`, `InventoryQueryServicesTest`

---

## Inventory Operation Guard

Internal helper shared by the inventory services: validates the role (seller or logistics operator), that the product is physical and the warehouse is active, that a seller only operates its own products and warehouses, and loads or creates the inventory record.

- **Class:** `application.domain.services.inventory.InventoryOperationGuard`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `SELLER`, `LOGISTICS_OPERATOR`
- **Operation:** `Inventory load(User actor, Long productId, Long warehouseId, boolean createIfMissing)`
- **Operation:** `void recordMovement(MovementType type, Inventory inventory, int quantity, User actor)`

**Collaborators (injected through the constructor):**

- `ProductRepositoryPort`
- `WarehouseRepositoryPort`
- `InventoryRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Producto no encontrado. / Almacen no encontrado. / No hay existencias de ese producto en ese almacén. |
| `InvalidDataException` | 400 | Los productos digitales no requieren gestión de inventario. / El almacen esta inactivo. |
| `UnauthorizedOperationException` | 403 | Un vendedor solo puede gestionar el stock de sus propios productos. / Un vendedor solo puede operar en sus propios almacenes. |

**Covered by tests:** `InventoryManagementServicesTest`, `RegisterInventoryEntryServiceTest`

---

## Register Inventory Entry

Registers stock entering a warehouse for a product and records the movement.

- **Class:** `application.domain.services.inventory.RegisterInventoryEntryService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Inventory register(User actor, Long productId, Long warehouseId, int quantity)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`
- `InventoryOperationGuard`

**Covered by tests:** `RegisterInventoryEntryServiceTest`

---

## Register Inventory Return

Returns stock to inventory when a returned item is received. Internal service.

- **Class:** `application.domain.services.inventory.RegisterInventoryReturnService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `void registerReturn(User operator, Long productId, Long warehouseId, int quantity)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Inventory not found. |

**Covered by tests:** `InternalInventoryServicesTest`, `ReturnFulfillmentServicesTest`

---

## Register Inventory Sale

Records the stock exit of the items of an order when its shipment is dispatched. Internal service.

- **Class:** `application.domain.services.inventory.RegisterInventorySaleService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `void registerSale(User operator, Long productId, Long warehouseId, int quantity)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Inventory not found. |

**Covered by tests:** `InternalInventoryServicesTest`, `ShippingServicesTest`

---

## Reserve Inventory

Reserves stock for the items of an order when the order is confirmed. Internal service.

- **Class:** `application.domain.services.inventory.ReserveInventoryService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `boolean hasStock(Long productId, int quantity)`
- **Operation:** `Long reserve(User buyer, Long productId, int quantity)`

**Collaborators (injected through the constructor):**

- `InventoryRepositoryPort`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InsufficientStockException` | 409 | Insufficient stock for product  |

**Covered by tests:** `InternalInventoryServicesTest`, `ConfirmOrderServiceTest`

---
