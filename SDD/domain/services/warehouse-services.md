# Warehouse Management Services

Package `application.domain.services.warehouse`. 3 classes. Generated from the source code.

## Change Warehouse Status

Activates or deactivates a warehouse.

- **Class:** `application.domain.services.warehouse.ChangeWarehouseStatusService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `Warehouse change(User actor, Long warehouseId, boolean active)`

**Collaborators (injected through the constructor):**

- `WarehouseRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Almacen no encontrado. |

**Covered by tests:** `WarehouseManagementServicesTest`

---

## Consult Warehouses

Lists the warehouses visible to the requesting role.

- **Class:** `application.domain.services.warehouse.ConsultWarehousesService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`, `SELLER`
- **Operation:** `List<Warehouse> consultWarehouses(User actor)`

**Collaborators (injected through the constructor):**

- `WarehouseRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `WarehouseManagementServicesTest`

---

## Create Warehouse

Creates a warehouse, either the marketplace warehouse or a warehouse that belongs to a seller.

- **Class:** `application.domain.services.warehouse.CreateWarehouseService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `Warehouse create(User actor, CreateWarehouseCommand command)`

**Collaborators (injected through the constructor):**

- `WarehouseRepositoryPort`
- `SellerRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | No se ha encontrado el vendedor. |

**Covered by tests:** `CreateWarehouseServiceTest`

---
