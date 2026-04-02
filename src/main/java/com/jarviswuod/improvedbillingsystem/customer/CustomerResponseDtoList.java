package com.jarviswuod.improvedbillingsystem.customer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer compact representation used in list responses")
public record CustomerResponseDtoList(
        @Schema(description = "Customer id", example = "1")
        Long id,
        @Schema(description = "Customer full name", example = "Ali Boual")
        String name
) {
}
