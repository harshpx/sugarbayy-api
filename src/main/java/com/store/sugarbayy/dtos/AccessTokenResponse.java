package com.store.sugarbayy.dtos;

import lombok.NonNull;

public record AccessTokenResponse(@NonNull String accessToken) {
    public static AccessTokenResponse of(@NonNull String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}