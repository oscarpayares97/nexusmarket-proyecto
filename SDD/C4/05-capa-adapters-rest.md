# Layer - REST Adapters

Package `application/adapters/rest/`. Input adapter: it translates HTTP to calls on the input ports and domain objects to JSON. It contains no business rules.

| Component | Role |
|---|---|
| `controllers/` | 7 controllers: `AuthRestController`, `PublicRestController`, `BuyerRestController`, `SellerRestController`, `LogisticsRestController`, `AdminRestController`, `SupervisorRestController` |
| `dtos/requests/RequestDTOs` | Java `record` bodies with Jakarta Bean Validation |
| `dtos/responses/ResponseDTOs` | Java `record` responses; the domain objects and the password hash are never serialized |
| `mappers/DomainRestMapper` | Static methods that convert domain objects to response DTOs |
| `exception/GlobalExceptionHandler` | Translates exceptions to HTTP status and a uniform JSON body |

## Rules

- A controller depends only on one input port (for example `BuyerPort`).
- The authenticated user arrives with `@AuthenticationPrincipal(expression = "user") User actor` and is passed as the first argument of every port operation.
- Creation endpoints answer 201, password changes answer 204, everything else answers 200.
- Controllers never catch exceptions: `GlobalExceptionHandler` does.

## Error mapping

| Exception | HTTP |
|---|---|
| `InvalidCredentialsException` | 401 |
| `UnauthorizedOperationException` | 403 |
| `EntityNotFoundException`, unknown route | 404 |
| `InvalidDataException`, validation failures, malformed JSON, wrong parameter type | 400 |
| `HttpRequestMethodNotSupportedException` | 405 |
| Any other `DomainException` (stock, status transition, duplicate) | 409 |
| Anything else | 500 |

Body: `timestamp`, `status`, `error`, `message`, `path`, `requestId`.

Generated detail: `SDD/adapters/Api-rest-endpoints.md`, `Rest-adapters.md`, `Rest-validation.md` and `Global-exception-handler.md`.