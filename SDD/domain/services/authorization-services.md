# Authorization Services

Package `application.domain.services.authorization`. 2 classes. Generated from the source code.

## Validate Ownership

Verifies that the acting user is the owner of the resource being accessed.

- **Class:** `application.domain.services.authorization.ValidateOwnershipService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `void requireOwner(User actor, Long ownerUserId)`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `UnauthorizedOperationException` | 403 | Solo el propietario puede realizar esta operación. |

**Covered by tests:** `AuthorizationServicesTest`, `InvoiceQueryServicesTest`, `CatalogManagementServicesTest`, `CatalogServicesTest`, `InventoryQueryServicesTest`, `PayOrderServiceTest`, `RequestReturnServiceTest`, `ShipmentQueryServicesTest`

---

## Validate Role Authorization

Verifies that the acting user exists, is active and holds one of the roles allowed for an operation (RG-01, RG-03).

- **Class:** `application.domain.services.authorization.ValidateRoleAuthorizationService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `void requireRole(User actor, UserRole... allowedRoles)`

**Business rules and notes (from the code):**

- RG-01 y RG-03: el usuario debe existir, estar activo y tener un rol permitido

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `UnauthorizedOperationException` | 403 | Es necesario ser un usuario autenticado. / El usuario no esta activo. / EL papel  |

**Covered by tests:** `AuthorizationServicesTest`, `InvoiceQueryServicesTest`, `BuyerManagementServicesTest`, `CatalogManagementServicesTest`, `CatalogServicesTest`, `InventoryManagementServicesTest`, `InventoryQueryServicesTest`, `RegisterInventoryEntryServiceTest`, `AddItemToCartServiceTest`, `ConfirmOrderServiceTest`, `OrderQueryServicesTest`, `PayOrderServiceTest`, `GenerateSalesReportServiceTest`, `RejectReturnServiceTest`, `RequestReturnServiceTest`, `ReturnFulfillmentServicesTest`, `ReturnQueryServicesTest`, `RegisterSellerServiceTest`, `ShipmentProgressServicesTest`, `ShipmentQueryServicesTest`, `ShippingServicesTest`, `ChangeUserPasswordServiceTest`, `ChangeUserStatusServiceTest`, `ConsultUserServiceTest`, `RegisterInternalUserServiceTest`, `CreateWarehouseServiceTest`, `WarehouseManagementServicesTest`

---
