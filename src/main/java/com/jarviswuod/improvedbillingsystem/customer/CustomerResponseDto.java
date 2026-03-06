package com.jarviswuod.improvedbillingsystem.customer;

public record CustomerResponseDto(
        Long id,

        String name,

        String email,

        String phone
) {
}
