package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Invoice payload used for update operations")
public record InvoiceUpdateDto(

        @Positive(message = "Amount must be positive and non-zero")
        @Schema(description = "Updated invoice amount", example = "2000.00")
        BigDecimal amount,

        @Future(message = "Due date must be in the future")
        @Schema(description = "Updated invoice due date (must be in the future)", example = "2026-12-31")
        LocalDate dueDate,

        @Schema(description = "Optional updated active customer id", example = "1")
        Long customerId
) {
}
