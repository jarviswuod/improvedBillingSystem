package com.jarviswuod.improvedbillingsystem.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Customer payload used for create and update operations")
public record CustomerDto(
        @NotBlank(message = "Name should not be empty")
        @Schema(description = "Customer full name", example = "Ali Boual")
        String name,

        @NotBlank(message = "Email should not be empty")
        @Email(message = "Email is invalid")
        @Schema(description = "Customer email address", example = "user@example.com")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$",
                message = "Email must be a valid format e.g. user@example.com"
        )
        String email,

        @Schema(description = "Customer phone number in international format", example = "+254712345678")
        @Pattern(
                regexp = "^\\+[1-9]\\d{6,14}$",
                message = "Phone must be in international format e.g. +254712345678"
        )
        String phone
) {
}
