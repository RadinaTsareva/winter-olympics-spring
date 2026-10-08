package com.example.winter_olympics.config;

import com.example.winter_olympics.exception.ForbiddenException;
import com.example.winter_olympics.exception.BadRequestException;
import com.example.winter_olympics.exception.NotFoundException;
import com.example.winter_olympics.exception.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
            UnauthorizedException exception
    ) {
        return response(HttpStatus.UNAUTHORIZED, "Unauthorized", exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(
            ForbiddenException exception
    ) {
        return response(HttpStatus.FORBIDDEN, "Forbidden", exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(
            NotFoundException exception
    ) {
        return response(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception exception) {
        String message = exception instanceof MethodArgumentNotValidException validation
                ? validation.getBindingResult().getFieldErrors().stream()
                    .findFirst().map(error -> error.getDefaultMessage())
                    .orElse("Request validation failed")
                : exception.getMessage();
        return response(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedRequest(
            HttpMessageNotReadableException exception
    ) {
        return response(HttpStatus.BAD_REQUEST, "Bad Request", "Request body is invalid or malformed");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataConflict(
            DataIntegrityViolationException exception
    ) {
        return response(HttpStatus.CONFLICT, "Conflict", "The operation conflicts with existing data");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException exception
    ) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "The server could not complete the request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "The server could not complete the request");
    }

    private ResponseEntity<Map<String, Object>> response(
            HttpStatus status, String error, String message
    ) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", error,
                "message", message == null ? error : message
        ));
    }
}
