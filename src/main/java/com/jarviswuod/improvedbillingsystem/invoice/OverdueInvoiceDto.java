package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Schema(description = "Overdue invoice projection (balance still outstanding)")
public record OverdueInvoiceDto(
        @Schema(description = "Invoice id", example = "1")
        Long invoiceNumber,
        @Schema(description = "Customer name")
        String customerName,
        @Schema(description = "Invoice total amount", example = "1500.00")
        BigDecimal amount,
        @Schema(description = "Amount already paid", example = "500.00")
        BigDecimal amountPaid,
        @Schema(description = "Outstanding balance = amount - amountPaid", example = "1000.00")
        BigDecimal balance,
        @Schema(description = "Invoice due date", example = "2026-01-01")
        LocalDate dueDate,
        @Schema(description = "Invoice status (OVERDUE)")
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