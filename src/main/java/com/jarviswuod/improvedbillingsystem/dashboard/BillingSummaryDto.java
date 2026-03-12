package com.jarviswuod.improvedbillingsystem.dashboard;

public record BillingSummaryDto(
        long totalCustomers,
        long totalInvoices,
        double totalAmountInvoiced,
        double totalAmountPaid,
        double outstandingBalance
) {
}
