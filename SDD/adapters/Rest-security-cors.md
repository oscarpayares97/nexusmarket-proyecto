# REST Security and CORS

Package `application.adapters.security`. Stateless security with JWT; there is no server session and therefore no logout service (the client discards the token). Generated from the source code.

## Access rules by route (SecurityConfig)

| Route pattern | Access |
|---|---|
| `"/api/v1/auth/**"` | public |
| `"/api/v1/public/**"` | public |
| `"/actuator/health", "/actuator/info"` | public |
| `"/api/v1/buyer/**"` | role `BUYER` |
| `"/api/v1/seller/**"` | role `SELLER` |
| `"/api/v1/logistics/**"` | role `LOGISTICS_OPERATOR` |
| `"/api/v1/admin/**"` | role `ADMINISTRATOR` |
| `"/api/v1/supervisor/**"` | role `SUPERVISOR` |
| `any other request` | authenticated |

Unauthenticated requests answer **401** and requests with an insufficient role answer **403**, both as JSON.

## CORS

| Setting | Value |
|---|---|
| `AllowedOrigins` | `List.of(frontendOrigins)` |
| `AllowedMethods` | `List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")` |
| `AllowedHeaders` | `List.of("Authorization", "Content-Type", "X-Request-Id")` |
| `ExposedHeaders` | `List.of("X-Request-Id")` |
| Property | `FRONTEND_ORIGIN:http://localhost:3000,http://127.0.0.1:3000,http://localhost:5173` |

## JWT (JwtProvider)

- **Algorithm:** HMAC-SHA (symmetric key)
- **Standard claims:** `sub` (user id), `jti`, `iat`, `exp`
- **Custom claims:** `email`, `documento`, `role`, `status`
- **Configuration properties:** `jwt.secret`, `jwt.expiration-ms`
- **Methods:** `generate`, `extractUserId`, `validateToken`, `getClaims`

## Request authentication flow (JwtAuthenticationFilter)

1. Read the `Authorization: Bearer <token>` header; if absent the request continues unauthenticated.
2. Validate signature and expiration of the token.
3. Extract the user id and load the **persisted** user through `LoadAuthenticatedUserService`. Design decision: the user is reloaded from the database on every request instead of trusting the claims, so a blocked or deactivated user loses access immediately.
4. Wrap the user in `AuthenticatedUserPrincipal` (authority `ROLE_<role>`) and store it in the security context.
5. Controllers receive the domain user with `@AuthenticationPrincipal(expression = "user")`.

Passwords are stored as BCrypt hashes through `BCryptPasswordServiceAdapter`.
