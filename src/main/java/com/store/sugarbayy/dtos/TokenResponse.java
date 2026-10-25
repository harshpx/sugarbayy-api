package com.store.sugarbayy.dtos;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.ResponseCookie;

@Builder
public record TokenResponse(@NonNull ResponseCookie refreshTokenCookie, @NonNull String accessToken ) {
    public static TokenResponse of(@NonNull ResponseCookie refreshTokenCookie, @NonNull String accessToken) {
        return new TokenResponse(refreshTokenCookie, accessToken);
    }
}
