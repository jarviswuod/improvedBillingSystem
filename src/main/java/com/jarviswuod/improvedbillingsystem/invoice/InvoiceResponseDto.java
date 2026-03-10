package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;
import com.jarviswuod.improvedbillingsystem.payment.PaymentInvoiceResponseDto;

import java.time.LocalDate;
import java.util.List;

public record InvoiceResponseDto(
        Double amount,
        LocalDate dueData,
        InvoiceStatus status,
        CustomerResponseDtoList customer,
        List<PaymentInvoiceResponseDto> payments
) {
}
