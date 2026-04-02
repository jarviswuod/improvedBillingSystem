package com.jarviswuod.improvedbillingsystem.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payment request payload used to create a payment for an invoice")
public record PaymentDto(

        @NotNull(message = "Payment can be only done to a valid invoice")
        @Schema(description = "Invoice id", example = "1")
        Long invoiceId,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive and non-zero")
        @Schema(description = "Payment amount", example = "500.00")
        BigDecimal amount,

        @NotNull(message = "Due date cannot be null")
        @PastOrPresent(message = "A payment’s date must be on or before the current date (no future payments)")
        @Schema(description = "Payment date (no future dates)", example = "2026-04-01")
        LocalDate paymentDate,

        @Nullable
        @Schema(description = "Payment method", nullable = true)
        PaymentMethod paymentMethod,

        @NotBlank(message = "Transaction Number must not be null")
        @Schema(description = "Unique transaction number (must be globally unique)", example = "TXN-123456")
        String transactionNumber
) {
}
