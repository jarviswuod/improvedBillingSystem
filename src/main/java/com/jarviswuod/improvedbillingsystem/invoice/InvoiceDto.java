package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Invoice payload used for create operations")
public record InvoiceDto(

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive and non-zero")
        @Schema(description = "Invoice amount", example = "1500.00")
        BigDecimal amount,

        @NotNull(message = "Due date cannot be null")
        @Future(message = "Due date must be in the future")
        @Schema(description = "Invoice due date (must be a future date)", example = "2026-12-31")
        LocalDate dueDate,

        @NotNull(message = "CustomerId cannot be null")
        @Schema(description = "Active customer id", example = "1")
        Long customerId
) {
}