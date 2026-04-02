package com.jarviswuod.improvedbillingsystem.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payment request payload used to update an existing payment")
public record UpdatePaymentDto(

        @Schema(description = "Optional invoice id to re-attach the payment to", nullable = true, example = "1")
        Long invoiceId,

        @Positive(message = "Amount must be positive and non-zero")
        @Schema(description = "Updated payment amount", example = "500.00")
        BigDecimal amount,

        @PastOrPresent(message = "A payment’s date must be on or before the current date (no future payments)")
        @Schema(description = "Updated payment date (no future dates)", example = "2026-04-01")
        LocalDate paymentDate,

        @Nullable
        @Schema(description = "Updated payment method", nullable = true)
        PaymentMethod paymentMethod,

        @Schema(description = "Updated transaction number (optional)", nullable = true)
        String transactionNumber
) {
}
