package com.jarviswuod.improvedbillingsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI())
                .thenReturn("/api/customers");
    }


    @Test
    void handleBusinessRuleShouldReturnConflictProblemDetail() {
        // Given
        BusinessRuleViolationException exception = new BusinessRuleViolationException("Duplicate email");

        // When
        ProblemDetail problemDetail = handler.handleBusinessRule(exception, request);

        // Then
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals("Business Rule Violation", problemDetail.getTitle());
        assertEquals(exception.getMessage(), problemDetail.getDetail());
        assertEquals(URI.create("/api/customers"), problemDetail.getInstance());
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
    }


    @Test
    void handleResourceNotFoundShouldReturnNotFoundProblemDetail() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Customer not found");

        // When
        ProblemDetail problemDetail = handler.handleNotFound(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Resource Not Found", problemDetail.getTitle());
        assertEquals(exception.getMessage(), problemDetail.getDetail());
        assertEquals(URI.create("/api/customers"), problemDetail.getInstance());
        assertThat(problemDetail.getProperties()).containsKey("timestamp");
    }


    @Test
    void handleNoHandlerFoundShouldReturnNotFoundProblemDetail() {
        // Given
        NoHandlerFoundException exception = new NoHandlerFoundException(
                "GET",
                "/api/missing",
                null
        );

        // When
        ProblemDetail problemDetail = handler.handleNotFound(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Resource Not Found", problemDetail.getTitle());
        assertEquals("No endpoint for GET /api/missing", problemDetail.getDetail());
        assertEquals(URI.create("/api/customers"), problemDetail.getInstance());
    }


    @Test
    void handleInvalidFormatShouldReturnBadRequestProblemDetail() {
        // Given
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("Invalid JSON");

        // When
        ProblemDetail problemDetail = handler.handleInvalidFormat(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid format exception", problemDetail.getTitle());
        assertEquals("Invalid format exception", problemDetail.getDetail());
        assertEquals(URI.create("/api/customers"), problemDetail.getInstance());
    }


    @Test
    void handleValidationShouldReturnBadRequestWithFieldErrors() throws NoSuchMethodException {
        // Given
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new ValidationTarget(), "validationTarget");
        bindingResult.addError(new FieldError(
                "validationTarget",
                "email",
                "Email is invalid"
        ));
        MethodParameter methodParameter =
                new MethodParameter(ValidationTarget.class.getDeclaredMethod("create", ValidationTarget.class), 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        // When
        ProblemDetail problemDetail = handler.handleValidation(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Validation Error", problemDetail.getTitle());
        assertEquals("Invalid request body", problemDetail.getDetail());
        assertThat(problemDetail.getProperties()).containsKey("fieldErrors");

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) problemDetail.getProperties().get("fieldErrors");
        assertEquals("Email is invalid", fieldErrors.get("email"));
    }


    @Test
    void handleConflictShouldReturnConflictProblemDetail() {
        // Given
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("duplicate key");

        // When
        ProblemDetail problemDetail = handler.handleConflict(exception, request);

        // Then
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals("Database Conflict", problemDetail.getTitle());
        assertEquals("Resource already exists", problemDetail.getDetail());
    }


    @Test
    void handleGeneralShouldReturnInternalServerErrorProblemDetail() {
        // Given
        Exception exception = new Exception("Unexpected");

        // When
        ProblemDetail problemDetail = handler.handleGeneral(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals("Internal Server Error", problemDetail.getTitle());
        assertEquals("Unexpected system error", problemDetail.getDetail());
    }


    private static class ValidationTarget {
        @SuppressWarnings("unused")
        void create(ValidationTarget target) {
        }
    }
}
