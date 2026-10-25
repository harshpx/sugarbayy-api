package com.store.sugarbayy.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactNumberDto(
        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid contact number")
        String contactNumber
) {
}
