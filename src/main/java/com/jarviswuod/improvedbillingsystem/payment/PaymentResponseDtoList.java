package com.jarviswuod.improvedbillingsystem.payment;

import java.time.LocalDate;

public record PaymentResponseDtoList(

        Double amount,
        PaymentMethod paymentMethod,
        String transactionNumber,
        LocalDate paymentDate,
        Long invoiceId
) {
}