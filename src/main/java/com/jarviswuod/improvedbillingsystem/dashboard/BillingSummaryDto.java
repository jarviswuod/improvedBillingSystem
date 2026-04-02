package com.jarviswuod.improvedbillingsystem.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Billing summary KPI response")
public record BillingSummaryDto(
        @Schema(description = "Total customers in the selected window", example = "10")
        long totalCustomers,
        @Schema(description = "Total invoices in the selected window", example = "25")
        long totalInvoices,
        @Schema(description = "Total invoiced amount in the selected window", example = "15000.00")
        BigDecimal totalAmountInvoiced,
        @Schema(description = "Total paid amount in the selected window", example = "8000.00")
        BigDecimal totalAmountPaid,
        @Schema(description = "Outstanding balance = total invoiced - total paid", example = "7000.00")
        BigDecimal outstandingBalance
) {
}
