package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record InvoiceDto(

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive and non-zero")
        Double amount,

        @NotNull(message = "Due date cannot be null")
        @Future(message = "Due date must be in the future")
        LocalDateTime dueDate,

        @NotNull(message = "CustomerId cannot be null")
        Long customerId
) {
}