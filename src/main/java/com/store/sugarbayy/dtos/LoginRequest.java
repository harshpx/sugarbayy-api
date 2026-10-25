package com.store.sugarbayy.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotBlank(message = "Identifier must not be empty") String identifier,
        @NotBlank(message = "Password must not be empty") String password
) {}