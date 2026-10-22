# Seller Management Services

Package `application.domain.services.seller`. 1 classes. Generated from the source code.

## Register Seller

Registers a seller user, its trade name and its first warehouse in a single operation. Restricted to administrators.

- **Class:** `application.domain.services.seller.RegisterSellerService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `Seller register(User actor, RegisterSellerCommand command)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `SellerRepositoryPort`
- `WarehouseRepositoryPort`
- `PasswordServicePort`
- `ValidateRoleAuthorizationService`

**Business rules and notes (from the code):**

- Validar todo antes de guardar nada
- Incorporación: el vendedor nace con su primera bodega

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InvalidDataException` | 400 | La contraseña debe tener al menos 8 caracteres. |
| `DuplicateEntityException` | 409 | Ya existe un usuario con ese documento.. / Ya existe un usuario con esa dirección de correo electrónico. |

**Covered by tests:** `RegisterSellerServiceTest`

---
