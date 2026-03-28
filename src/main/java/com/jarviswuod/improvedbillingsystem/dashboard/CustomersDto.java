package com.jarviswuod.improvedbillingsystem.dashboard;

import java.math.BigDecimal;

public record CustomersDto(
        String customerName,
        BigDecimal totalPaid
) {
}
