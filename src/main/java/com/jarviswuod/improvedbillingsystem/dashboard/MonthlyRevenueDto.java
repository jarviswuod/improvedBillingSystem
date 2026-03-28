package com.jarviswuod.improvedbillingsystem.dashboard;

import java.math.BigDecimal;

public record MonthlyRevenueDto(
        String month,
        BigDecimal total
) {
}
