package com.jarviswuod.improvedbillingsystem.payment;

import java.time.LocalDate;

public record PaymentInvoiceResponseDto(
        Double amount,
        LocalDate paymentDate
) {
}
