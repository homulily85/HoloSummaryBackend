package com.holosumary.holosummary.advice;

import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest request) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toViolation)
                .toList();

        return ResponseEntity.badRequest()
                .body(new ApiError("Validation failed", "Bad Request", request.getRequestURI(),
                        Instant.now(), violations));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(violation -> new FieldViolation(violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(new ApiError("Validation failed", "Bad Request", request.getRequestURI(),
                        Instant.now(), violations));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex,
                                                   HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage(), "Not Found", request.getRequestURI(),
                        Instant.now(), List.of()));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> handleExternalService(ExternalServiceException ex,
                                                          HttpServletRequest request) {
        HttpStatus status = ex.getStatus();

        if (status.is5xxServerError()) {
            logger.error("External service 5xx error at {}: {}", request.getRequestURI(), ex.getResponseBody(), ex);
        } else {
            logger.warn("External service error at {}: {}", request.getRequestURI(), ex.getResponseBody());
        }

        return ResponseEntity.status(status)
                .body(new ApiError(ex.getMessage(), status.getReasonPhrase(),
                        request.getRequestURI(),
                        Instant.now(), List.of()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleHttpClientError(HttpClientErrorException ex,
                                                          HttpServletRequest request) {
        logger.error("HTTP client error at {}: {}", request.getRequestURI(), ex.getResponseBodyAsString(), ex);

        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApiError(ex.getStatusText(), ex.getStatusCode().toString(),
                        request.getRequestURI(), Instant.now(), List.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {

        logger.error("Unexpected server error at {}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Unexpected server error", "Internal Server Error",
                        request.getRequestURI(), Instant.now(), List.of()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(NoResourceFoundException ex,
                                                          HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Endpoint not found", "Not Found",
                        request.getRequestURI(), Instant.now(), List.of()));
    }

    private FieldViolation toViolation(FieldError error) {
        String message = error.getDefaultMessage();
        return new FieldViolation(error.getField(), message == null ? "Invalid value" : message);
    }

    public record ApiError(String message, String error, String path, Instant timestamp,
                           List<FieldViolation> violations) {
    }

    public record FieldViolation(String field, String message) {
    }
}
