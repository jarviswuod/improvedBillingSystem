package com.jarviswuod.improvedbillingsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/customers");
    }

    @Test
    void handleBusinessRuleShouldReturnConflictApiError() {
        // Given
        BusinessRuleViolationException exception = new BusinessRuleViolationException("Duplicate email");

        // When
        ResponseEntity<ApiError> response = handler.handleBusinessRule(exception, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.BUSINESS_RULE_VIOLATION, response.getBody().getErrorCode());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
        assertEquals("/api/customers", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleInvalidFormatShouldReturnBadRequestApiError() {
        // Given
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("Invalid JSON");

        // When
        ResponseEntity<ApiError> response = handler.handleInvalidFormat(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.INVALID_FORMAT, response.getBody().getErrorCode());
        assertEquals("Invalid format exception", response.getBody().getMessage());
    }

    @Test
    void handleNoHandlerFoundShouldReturnNotFoundApiError() {
        // Given
        NoHandlerFoundException exception = new NoHandlerFoundException(
                "GET",
                "/api/missing",
                null
        );

        // When
        ResponseEntity<ApiError> response = handler.handleNotFound(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, response.getBody().getErrorCode());
        assertEquals("No endpoint for GET /api/missing", response.getBody().getMessage());
    }

    @Test
    void handleResourceNotFoundShouldReturnNotFoundApiError() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Customer not found");

        // When
        ResponseEntity<ApiError> response = handler.handleNotFound(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, response.getBody().getErrorCode());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    void handleValidationShouldReturnBadRequestApiErrorWithFieldErrors() throws NoSuchMethodException {
        // Given
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new ValidationTarget(), "validationTarget");
        bindingResult.addError(new FieldError(
                "validationTarget",
                "name",
                "Name should not be empty"
        ));
        MethodParameter methodParameter =
                new MethodParameter(ValidationTarget.class.getDeclaredMethod("create", ValidationTarget.class), 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        // When
        ResponseEntity<ApiError> response = handler.handleValidation(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.VALIDATION_ERROR, response.getBody().getErrorCode());
        assertEquals("Invalid request", response.getBody().getMessage());
        assertThat(response.getBody().getFieldErrors())
                .containsEntry("name", "Name should not be empty");
    }

    @Test
    void handleDataIntegrityShouldReturnConflictApiError() {
        // Given
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("duplicate key");

        // When
        ResponseEntity<ApiError> response = handler.handleDataIntegrity(exception, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.DATABASE_CONFLICT, response.getBody().getErrorCode());
        assertEquals("Resource already exists", response.getBody().getMessage());
    }

    @Test
    void handleGeneralShouldReturnInternalServerErrorApiError() {
        // Given
        Exception exception = new Exception("Unexpected");

        // When
        ResponseEntity<ApiError> response = handler.handleGeneral(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, response.getBody().getErrorCode());
        assertEquals("Unexpected system error", response.getBody().getMessage());
    }

    private static class ValidationTarget {
        @SuppressWarnings("unused")
        void create(ValidationTarget target) {
        }
    }
}
