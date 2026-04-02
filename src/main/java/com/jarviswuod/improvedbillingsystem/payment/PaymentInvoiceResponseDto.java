package com.jarviswuod.improvedbillingsystem.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payment projection included inside invoice response")
public record PaymentInvoiceResponseDto(
        @Schema(description = "Payment amount", example = "500.00")
        BigDecimal amount,

        @Schema(description = "Payment date", example = "2026-04-01")
        LocalDate paymentDate
) {
}
