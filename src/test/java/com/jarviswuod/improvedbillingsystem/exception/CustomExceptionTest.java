package com.jarviswuod.improvedbillingsystem.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomExceptionTest {

    @Test
    void businessRuleViolationExceptionShouldKeepMessage() {
        // Given
        String message = "Business rule failed";

        // When
        BusinessRuleViolationException exception = new BusinessRuleViolationException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void resourceNotFoundExceptionShouldKeepMessage() {
        // Given
        String message = "Resource not found";

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }
}
