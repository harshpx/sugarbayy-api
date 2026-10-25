package com.store.sugarbayy.services;

import com.store.sugarbayy.dtos.TokenResponse;
import com.store.sugarbayy.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface RefreshTokenService {
    ResponseCookie generate(Long usedId, Role role);
    TokenResponse refresh(HttpServletRequest request);
    ResponseCookie revoke(HttpServletRequest request);
}
