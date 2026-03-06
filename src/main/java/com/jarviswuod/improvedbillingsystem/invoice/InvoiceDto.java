package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record InvoiceDto(
        @NotBlank(message = "Amount cannot be empty")
        @Positive(message = "Amount must be positive and non-zero")
        Double amount,

        @NotBlank(message = "Due date cannot be empty")
        @Future(message = "Due date must be in the future")
        LocalDateTime dueDate,

        @NotBlank
        Long customerId
) {
}
