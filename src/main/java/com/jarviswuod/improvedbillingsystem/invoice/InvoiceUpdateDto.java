package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceUpdateDto(

        @Positive(message = "Amount must be positive and non-zero")
        BigDecimal amount,

        @Future(message = "Due date must be in the future")
        LocalDate dueDate,

        Long customerId
) {
}
