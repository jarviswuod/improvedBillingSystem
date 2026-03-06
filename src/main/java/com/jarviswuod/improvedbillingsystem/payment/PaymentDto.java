package com.jarviswuod.improvedbillingsystem.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record PaymentDto(

        @NotBlank(message = "Payment is only to a valid invoice")
        Long invoiceId,

        @NotBlank
        LocalDateTime paymentDate,

        @NotBlank
        @Positive(message = "Payment amount must be positive")
        Double amount,

        @PastOrPresent(message = "A payment’s date must be on or before the current date (no future payments)")
        PaymentMethod paymentMethod,

        @NotBlank
        String transactionNumber
) {
}
