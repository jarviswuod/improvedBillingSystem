package com.jarviswuod.improvedbillingsystem.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentInvoiceResponseDto(
        BigDecimal amount,
        LocalDate paymentDate
) {
}
