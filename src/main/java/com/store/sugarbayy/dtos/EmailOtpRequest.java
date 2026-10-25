package com.store.sugarbayy.dtos;

import com.store.sugarbayy.annotations.validEmail.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailOtpRequest(
        @ValidEmail
        String email,
        @NotBlank(message = "OTP must not be blank")
        String otp
) {}
