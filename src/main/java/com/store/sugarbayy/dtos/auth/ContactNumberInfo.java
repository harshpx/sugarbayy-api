package com.store.sugarbayy.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactNumberInfo(
        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid contact number")
        String contactNumber
) {
}
