package com.store.sugarbayy.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Duration;
import java.util.Arrays;

public class RefreshTokenHelper {
    public static final Duration expiryDuration = Duration.ofDays(10);
    public static String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if  (cookies == null) {
            throw new BadCredentialsException("No token found");
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("No token found"));
    }

    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true).secure(true)
                .path("/auth").sameSite("None")
                .maxAge(expiryDuration)
                .build();
    }

    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(true)
                .path("/auth").sameSite("None")
                .maxAge(0)
                .build();
    }
}
