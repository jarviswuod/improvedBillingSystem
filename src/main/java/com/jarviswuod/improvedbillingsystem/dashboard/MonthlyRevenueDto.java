package com.jarviswuod.improvedbillingsystem.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Monthly revenue projection for dashboard responses")
public record MonthlyRevenueDto(
        @Schema(description = "Month key (YYYY-MM)", example = "2026-04")
        String month,

        @Schema(description = "Total amount paid in that month", example = "12000.00")
        BigDecimal total
) {
}
