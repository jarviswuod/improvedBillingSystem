package com.jarviswuod.improvedbillingsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiError> handleBusinessRule(
            BusinessRuleViolationException ex,
            HttpServletRequest request
    ) {

        log.warn("Business rule violation: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(ErrorCode.BUSINESS_RULE_VIOLATION)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidFormat(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        log.warn("Invalid format exception: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.INVALID_FORMAT)
                .message("Invalid format exception")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn("Resource not found {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(ErrorCode.RESOURCE_NOT_FOUND)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        log.warn("Validation failed {}", fieldErrors);

        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.VALIDATION_ERROR)
                .message("Invalid request")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        log.error("Database conflict", ex);

        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(ErrorCode.DATABASE_CONFLICT)
                .message("Resource already exists")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected error", ex);

        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Unexpected system error")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}