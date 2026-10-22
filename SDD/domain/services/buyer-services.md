# Buyer Management Services

Package `application.domain.services.buyer`. 3 classes. Generated from the source code.

## Add Buyer Address

Adds an additional delivery address to the authenticated buyer.

- **Class:** `application.domain.services.buyer.AddBuyerAddressService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `BUYER`
- **Operation:** `Buyer add(User actor, String address)`

**Collaborators (injected through the constructor):**

- `BuyerRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Comprador no encontrado. |

**Covered by tests:** `BuyerManagementServicesTest`

---

## Change Buyer Commercial Status

Enables or suspends the ability of a buyer to purchase.

- **Class:** `application.domain.services.buyer.ChangeBuyerCommercialStatusService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `Buyer change(User actor, Long buyerUserId, CommercialStatus newStatus)`

**Collaborators (injected through the constructor):**

- `BuyerRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Comprador no encontrado. |

**Covered by tests:** `UseCaseImplDelegationTest`, `BuyerManagementServicesTest`

---

## Register Buyer

Registers a new buyer user together with the buyer profile and its primary delivery address.

- **Class:** `application.domain.services.buyer.RegisterBuyerService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `Buyer register(RegisterBuyerCommand command)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `BuyerRepositoryPort`
- `PasswordServicePort`

**Business rules and notes (from the code):**

- Validar todo antes de guardar
- Crear el usuario (el modelo valida nombre, correo, etc.) y guardarlo
- Crear y guardar el comprador asociado

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InvalidDataException` | 400 | La contraseña no puede tener mas de 8 caracteres. |
| `DuplicateEntityException` | 409 | Ya existe un usuario con ese documento. / Ya existe un usuario con este email. |

**Covered by tests:** `UseCaseImplDelegationTest`, `RegisterBuyerServiceTest`

---
