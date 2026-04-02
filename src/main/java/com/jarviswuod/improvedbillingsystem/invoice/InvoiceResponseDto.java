package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;
import com.jarviswuod.improvedbillingsystem.payment.PaymentInvoiceResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Full invoice representation including customer and payments")
public record InvoiceResponseDto(
        @Schema(description = "Invoice total amount", example = "1500.00")
        BigDecimal amount,

        @Schema(description = "Invoice due date", example = "2026-12-31")
        LocalDate dueData,

        @Schema(description = "Invoice status", example = "PENDING")
        InvoiceStatus status,

        @Schema(description = "Invoice customer")
        CustomerResponseDtoList customer,

        @Schema(description = "Payments recorded for the invoice")
        List<PaymentInvoiceResponseDto> payments
) {
}
