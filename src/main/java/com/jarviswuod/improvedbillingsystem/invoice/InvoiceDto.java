package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceDto(

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive and non-zero")
        BigDecimal amount,

        @NotNull(message = "Due date cannot be null")
        @Future(message = "Due date must be in the future")
        LocalDate dueDate,

        @NotNull(message = "CustomerId cannot be null")
        Long customerId
) {
}