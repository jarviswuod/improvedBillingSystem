package com.jarviswuod.improvedbillingsystem.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supported payment methods")
public enum PaymentMethod {

    MPESA, CARD, BANK_TRANSFER
}
