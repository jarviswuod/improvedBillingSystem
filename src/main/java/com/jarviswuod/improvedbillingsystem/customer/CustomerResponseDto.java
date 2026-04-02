package com.jarviswuod.improvedbillingsystem.customer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer data returned to clients")
public record CustomerResponseDto(
        @Schema(description = "Customer id", example = "1")
        Long id,

        @Schema(description = "Customer full name", example = "Ali Boual")
        String name,

        @Schema(description = "Customer email", example = "user@example.com")
        String email,

        @Schema(description = "Customer phone number", example = "+254712345678")
        String phone
) {
}
