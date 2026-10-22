package application.adapters.rest.exception;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidDataException;
import application.domain.exceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Traduce las excepciones del dominio a respuestas HTTP. Spring elige el handler más específico. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {
        return respond(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedOperationException ex, HttpServletRequest request) {
        return respond(HttpStatus.FORBIDDEN, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidData(
            InvalidDataException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respond(HttpStatus.BAD_REQUEST, message, request, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedBody(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "Malformed JSON request body.", request, ex);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMethodValidation(
            HandlerMethodValidationException ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "Invalid request parameter.", request, ex);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(
            NoResourceFoundException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND,
                "Resource not found: " + request.getMethod() + " " + request.getRequestURI(), request, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error {} {}", request.getMethod(), request.getRequestURI(), ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.", request, null);
    }

    private ResponseEntity<Map<String, Object>> respond(HttpStatus status, String message,
                                                         HttpServletRequest request, Exception ex) {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (ex != null) {
            log.warn("{} [requestId={}] {} {} -> {}: {}", status.value(), requestId,
                    request.getMethod(), request.getRequestURI(),
                    ex.getClass().getSimpleName(), ex.getMessage());
        }
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message == null ? "" : message,
                "path", request.getRequestURI(),
                "requestId", requestId);
        return ResponseEntity.status(status).body(body);
    }
}