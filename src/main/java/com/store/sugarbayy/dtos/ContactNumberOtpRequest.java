package com.store.sugarbayy.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ContactNumberOtpRequest(
        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid contact number")
        String contactNumber,
        @NotBlank
        String otp
) {}
