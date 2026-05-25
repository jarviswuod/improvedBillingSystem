package com.jarviswuod.improvedbillingsystem.exception;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiErrorTest {

    @Test
    void builderShouldCreateApiErrorWithAllFields() {
        // Given
        Instant timestamp = Instant.now();
        Map<String, String> fieldErrors = Map.of("email", "Email is invalid");

        // When
        ApiError apiError = ApiError.builder()
                .timestamp(timestamp)
                .status(400)
                .errorCode(ErrorCode.VALIDATION_ERROR)
                .message("Invalid request")
                .path("/api/customers")
                .traceId("trace-123")
                .fieldErrors(fieldErrors)
                .build();

        // Then
        assertEquals(timestamp, apiError.getTimestamp());
        assertEquals(400, apiError.getStatus());
        assertEquals(ErrorCode.VALIDATION_ERROR, apiError.getErrorCode());
        assertEquals("Invalid request", apiError.getMessage());
        assertEquals("/api/customers", apiError.getPath());
        assertEquals("trace-123", apiError.getTraceId());
        assertEquals(fieldErrors, apiError.getFieldErrors());
    }
}
