package com.jarviswuod.improvedbillingsystem.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OverdueInvoiceDto(
        Long invoiceNumber,
        String customerName,
        BigDecimal amount,
        BigDecimal amountPaid,
        BigDecimal balance,
        LocalDate dueDate,
        int daysOverdue,
        InvoiceStatus status
) {

}
