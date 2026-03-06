package com.jarviswuod.improvedbillingsystem.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerDto(
        @NotBlank(message = "Name should not be empty")
        String name,

        @NotBlank(message = "Email should not be empty")
        @Email(message = "Email is invalid")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$",
                message = "Email must be a valid format e.g. user@example.com"
        )
        String email,

        @Pattern(
                regexp = "^\\+[1-9]\\d{6,14}$",
                message = "Phone must be in international format e.g. +254712345678"
        )
        String phone
) {
}
