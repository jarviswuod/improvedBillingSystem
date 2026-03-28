package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponseDtoList(
        BigDecimal amount,
        LocalDate dueData,
        InvoiceStatus status,
        CustomerResponseDtoList customer
) {
}
