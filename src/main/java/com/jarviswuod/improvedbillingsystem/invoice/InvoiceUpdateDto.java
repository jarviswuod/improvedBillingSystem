package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InvoiceUpdateDto(

        @Positive(message = "Amount must be positive and non-zero")
        Double amount,

        @Future(message = "Due date must be in the future")
        LocalDate dueDate,

        Long customerId
) {
}
