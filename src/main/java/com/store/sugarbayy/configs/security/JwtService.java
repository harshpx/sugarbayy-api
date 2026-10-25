package com.store.sugarbayy.configs.security;

import com.store.sugarbayy.enums.Role;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String generateToken(Long userId, Role role);
    Claims extractClaims(String token);
    boolean isTokenValid(String token);
}
