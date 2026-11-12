package com.store.sugarbayy.dtos.auth;

import com.store.sugarbayy.annotations.validEmail.ValidEmail;

public record EmailInfo(@ValidEmail String email) {
}
