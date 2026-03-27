package com.jarviswuod.improvedbillingsystem.invoice;

import java.time.LocalDate;

public record OverdueInvoiceDto(
        Long invoiceNumber,
        String customerName,
        Double amount,
        Double amountPaid,
        Double balance,
        LocalDate dueDate,
        Integer daysOverdue,
        Short status
) {
}
