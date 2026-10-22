# Layer - Security Adapter

Package `application/adapters/security/`. Stateless security with JWT; there is no server session, therefore no logout service (the client discards the token).

| Class | Role |
|---|---|
| `SecurityConfig` | Filter chain: CSRF disabled, stateless sessions, JSON 401 and 403 handlers, route rules, CORS source, BCrypt bean |
| `JwtProvider` | Implements `JwtServicePort`: issues and validates HMAC signed tokens |
| `JwtAuthenticationFilter` | Validates the token, reloads the user and fills the security context |
| `AuthenticatedUserPrincipal` | `UserDetails` wrapper exposing the domain `User` and the authority `ROLE_<role>` |
| `BCryptPasswordServiceAdapter` | Implements `PasswordServicePort` (encode and matches) |

## Route rules

| Prefix | Access |
|---|---|
| `/api/v1/auth/**`, `/api/v1/public/**`, `/actuator/health`, `/actuator/info` | public |
| `/api/v1/buyer/**` | `BUYER` |
| `/api/v1/seller/**` | `SELLER` |
| `/api/v1/logistics/**` | `LOGISTICS_OPERATOR` |
| `/api/v1/admin/**` | `ADMINISTRATOR` |
| `/api/v1/supervisor/**` | `SUPERVISOR` |
| any other request | authenticated |

## Design decision: the user is reloaded on every request

Instead of rebuilding the user from the token claims, `JwtAuthenticationFilter` validates the token and loads the persisted user through `LoadAuthenticatedUserService`. A blocked or deactivated user therefore loses access immediately, even with a valid token. The cost is one primary key query per request.

## CORS

Allowed origins come from the property `FRONTEND_ORIGIN` (default `http://localhost:3000,http://127.0.0.1:3000,http://localhost:5173`); allowed methods GET, POST, PUT, PATCH, DELETE, OPTIONS; allowed headers `Authorization`, `Content-Type`, `X-Request-Id`.

## Passwords and the seed secret

Passwords are stored as BCrypt hashes. The JWT secret has a public academic default in `application.properties`; in a real deployment it must be supplied through the environment.

Generated detail: `SDD/adapters/Rest-security-cors.md`.