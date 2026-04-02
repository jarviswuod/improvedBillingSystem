package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Invoice payment status")
public enum InvoiceStatus {

    PENDING, PARTIALLY_PAID, PAID, OVERDUE
}
