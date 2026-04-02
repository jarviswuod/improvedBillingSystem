package com.jarviswuod.improvedbillingsystem.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Top customer projection for dashboard responses")
public record CustomersDto(
        @Schema(description = "Customer name", example = "Ali Boual")
        String customerName,
        @Schema(description = "Total amount paid by the customer in the selected window", example = "8000.00")
        BigDecimal totalPaid
) {
}
