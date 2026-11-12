package com.store.sugarbayy.services.refreshToken;

import com.store.sugarbayy.dtos.auth.TokenResponse;
import com.store.sugarbayy.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface RefreshTokenService {
    ResponseCookie generate(Long usedId, Role role);
    TokenResponse refresh(HttpServletRequest request);
    ResponseCookie revoke(HttpServletRequest request);
}
