package com.jarviswuod.improvedbillingsystem.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponseDto(

        Long id,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String transactionNumber,
        LocalDate paymentDate,
        Long invoiceId
) {
}