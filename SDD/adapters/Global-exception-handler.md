# Global Exception Handler

Class `application.adapters.rest.exception.GlobalExceptionHandler` (`@RestControllerAdvice`). Translates domain and framework exceptions into a uniform JSON response. Spring selects the most specific handler. Generated from the source code.

## Response body

| Field | Meaning |
|---|---|
| `timestamp` | Moment of the error |
| `status` | HTTP status code |
| `error` | HTTP reason phrase |
| `message` | Human readable message (the domain exception message) |
| `path` | Requested path |
| `requestId` | Value of the `X-Request-Id` header or a generated UUID, also written to the log |

## Exception mapping

| Exception | HTTP | Handler method |
|---|---|---|
| `InvalidCredentialsException` | 401 | `handleInvalidCredentials` |
| `UnauthorizedOperationException` | 403 | `handleUnauthorized` |
| `EntityNotFoundException` | 404 | `handleNotFound` |
| `InvalidDataException` | 400 | `handleInvalidData` |
| `DomainException` | 409 | `handleDomain` |
| `MethodArgumentNotValidException` | 400 | `handleValidation` |
| `HttpMessageNotReadableException` | 400 | `handleMalformedBody` |
| `HandlerMethodValidationException` | 400 | `handleMethodValidation` |
| `NoResourceFoundException` | 404 | `handleNoResource` |
| `HttpRequestMethodNotSupportedException` | 405 | `handleMethodNotSupported` |
| `MethodArgumentTypeMismatchException` | 400 | `handleTypeMismatch` |
| `Exception` | 500 | `handleUnexpected` |

Domain exceptions not listed explicitly fall to `DomainException` and answer **409 Conflict** (a valid request rejected by a business rule).
