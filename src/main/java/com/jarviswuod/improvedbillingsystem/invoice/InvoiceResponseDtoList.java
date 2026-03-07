package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;

import java.time.LocalDateTime;

public record InvoiceResponseDtoList(
        Double amount,
        LocalDateTime dueData,
        InvoiceStatus status,
        CustomerResponseDtoList customer
) {
}
