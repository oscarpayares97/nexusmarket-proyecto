# User and Authentication Management Services

Package `application.domain.services.user`. 6 classes. Generated from the source code.

## Change User Password

Changes the password of the authenticated user after verifying the current one.

- **Class:** `application.domain.services.user.ChangeUserPasswordService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `values`
- **Operation:** `void change(User actor, String currentPassword, String newPassword)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `PasswordServicePort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `EntityNotFoundException` | 404 | Usuario no encontrado. |
| `InvalidCredentialsException` | 401 | L contraseña no es correcta. |
| `InvalidDataException` | 400 | La nueva contraseña tiene mas de 8 caracteres. |

**Covered by tests:** `UseCaseImplDelegationTest`, `ChangeUserPasswordServiceTest`

---

## Change User Status

Activates, deactivates or blocks a user. An administrator cannot change its own status.

- **Class:** `application.domain.services.user.ChangeUserStatusService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `User change(User actor, Long userId, UserStatus newStatus)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InvalidDataException` | 400 | Un administrador no puede modificar su propio estado. |
| `EntityNotFoundException` | 404 | Usuario no encontrado. |

**Covered by tests:** `ChangeUserStatusServiceTest`

---

## Consult User

Lists the system users for back-office roles.

- **Class:** `application.domain.services.user.ConsultUserService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`, `SUPERVISOR`
- **Operation:** `List<User> consultUsers(User actor)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `ValidateRoleAuthorizationService`

**Covered by tests:** `UseCaseImplDelegationTest`, `ConsultUserServiceTest`

---

## Load Authenticated User

Resolves the persisted user that owns a valid token so that every request is evaluated against the current database state.

- **Class:** `application.domain.services.user.LoadAuthenticatedUserService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `User load(String token)`

**Collaborators (injected through the constructor):**

- `JwtServicePort`
- `UserRepositoryPort`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InvalidCredentialsException` | 401 | Invalid token. |
| `UnauthorizedOperationException` | 403 | The user is not active. |

**Covered by tests:** `LoadAuthenticatedUserServiceTest`

---

## Login

Authenticates a user with email and password and issues a signed JWT.

- **Class:** `application.domain.services.user.LoginService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** No role check (public operation or internal service invoked by other services)
- **Operation:** `AuthenticationResult login(String email, String rawPassword)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `PasswordServicePort`
- `JwtServicePort`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InvalidCredentialsException` | 401 | Invalid credentials. |
| `UnauthorizedOperationException` | 403 | The user is not active. |

**Covered by tests:** `UseCaseImplDelegationTest`, `LoginServiceTest`

---

## Register Internal User

Creates an internal system user (administrator, supervisor or logistics operator). Restricted to administrators.

- **Class:** `application.domain.services.user.RegisterInternalUserService`
- **Input port:** none (used directly by the role use cases or by other services)
- **Authorization:** `ADMINISTRATOR`
- **Operation:** `User register(User actor, RegisterInternalUserCommand command)`

**Collaborators (injected through the constructor):**

- `UserRepositoryPort`
- `PasswordServicePort`
- `ValidateRoleAuthorizationService`

**Exceptions:**

| Exception | HTTP | Message |
|---|---|---|
| `InvalidDataException` | 400 | Los compradores y los vendedores tienen su propio registro. / La contraseña debe tener al menos 8 caracteres.. |
| `DuplicateEntityException` | 409 | Ya existe un usuario con ese documento. / Ya existe un usuario con esa dirección de correo electrónico.. |

**Covered by tests:** `RegisterInternalUserServiceTest`

---
