package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;

import java.time.LocalDate;

public record InvoiceResponseDtoList(
        Double amount,
        LocalDate dueData,
        InvoiceStatus status,
        CustomerResponseDtoList customer
) {
}
