package com.jarviswuod.improvedbillingsystem.dashboard;

import java.math.BigDecimal;

public record BillingSummaryDto(
        long totalCustomers,
        long totalInvoices,
        BigDecimal totalAmountInvoiced,
        BigDecimal totalAmountPaid,
        BigDecimal outstandingBalance
) {
}
