package com.jarviswuod.improvedbillingsystem.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public record OverdueInvoiceDto(
        Long invoiceNumber,
        String customerName,
        BigDecimal amount,
        BigDecimal amountPaid,
        BigDecimal balance,
        LocalDate dueDate,
        InvoiceStatus status
) {
    public long getDaysOverdue() {
        if (dueDate == null) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }


    public OverdueInvoiceDto(
            Long invoiceNumber,
            String customerName,
            BigDecimal amount,
            BigDecimal amountPaid,
            BigDecimal balance,
            LocalDate dueDate,
            String status
    ) {
        this(
                invoiceNumber,
                customerName,
                amount,
                amountPaid,
                balance,
                dueDate,
                InvoiceStatus.valueOf(status)
        );
    }
}