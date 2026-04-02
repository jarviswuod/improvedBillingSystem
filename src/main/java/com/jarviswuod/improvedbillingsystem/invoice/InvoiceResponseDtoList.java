package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Compact invoice representation used in list endpoints")
public record InvoiceResponseDtoList(
        @Schema(description = "Invoice total amount", example = "1500.00")
        BigDecimal amount,

        @Schema(description = "Invoice due date", example = "2026-12-31")
        LocalDate dueData,

        @Schema(description = "Invoice status", example = "PENDING")
        InvoiceStatus status,

        @Schema(description = "Invoice customer (compact)")
        CustomerResponseDtoList customer
) {
}
