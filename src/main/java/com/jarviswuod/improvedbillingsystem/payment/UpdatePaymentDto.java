package com.jarviswuod.improvedbillingsystem.payment;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePaymentDto(

        Long invoiceId,

        @Positive(message = "Amount must be positive and non-zero")
        BigDecimal amount,

        @PastOrPresent(message = "A payment’s date must be on or before the current date (no future payments)")
        LocalDate paymentDate,

        @Nullable
        PaymentMethod paymentMethod,

        String transactionNumber
) {
}
