package com.store.sugarbayy.configurations.security.helpers;

import com.store.sugarbayy.enums.Role;
import lombok.NonNull;

import java.util.Objects;

public record AuthPrincipal(@NonNull Long userId, @NonNull Role role) {
    public AuthPrincipal(String userId, String role) {
        this(
            Long.parseLong(Objects.requireNonNull(userId, "userId must not be null")),
            Role.valueOf(Objects.requireNonNull(role, "role must not be null"))
        );
    }
}