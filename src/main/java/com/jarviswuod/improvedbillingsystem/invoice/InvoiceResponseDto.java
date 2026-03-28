package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;
import com.jarviswuod.improvedbillingsystem.payment.PaymentInvoiceResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponseDto(
        BigDecimal amount,
        LocalDate dueData,
        InvoiceStatus status,
        CustomerResponseDtoList customer,
        List<PaymentInvoiceResponseDto> payments
) {
}
