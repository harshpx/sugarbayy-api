package com.store.sugarbayy.dtos;

import com.store.sugarbayy.annotations.validEmail.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDto(@ValidEmail String email) {
}
