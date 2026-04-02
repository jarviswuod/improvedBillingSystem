package com.jarviswuod.improvedbillingsystem.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Compact payment representation used in list responses")
public record PaymentResponseDtoList(

        @Schema(description = "Payment amount", example = "500.00")
        BigDecimal amount,

        @Schema(description = "Payment method")
        PaymentMethod paymentMethod,

        @Schema(description = "Transaction number", example = "TXN-123456")
        String transactionNumber,

        @Schema(description = "Payment date", example = "2026-04-01")
        LocalDate paymentDate,

        @Schema(description = "Associated invoice id", example = "10")
        Long invoiceId
) {
}