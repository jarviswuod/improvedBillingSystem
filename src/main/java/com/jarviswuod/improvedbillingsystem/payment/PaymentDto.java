package com.jarviswuod.improvedbillingsystem.payment;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PaymentDto(

        @NotNull(message = "Payment can be only done to a valid invoice")
        Long invoiceId,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive and non-zero")
        Double amount,

        @NotNull(message = "Due date cannot be null")
        @PastOrPresent(message = "A payment’s date must be on or before the current date (no future payments)")
        LocalDate paymentDate,

        @Nullable
        PaymentMethod paymentMethod,

        @NotBlank(message = "Transaction Number must not be null")
        String transactionNumber
) {
}
