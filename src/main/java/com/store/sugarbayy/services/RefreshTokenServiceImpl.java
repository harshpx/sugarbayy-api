package com.store.sugarbayy.services;

import com.store.sugarbayy.configs.Common;
import com.store.sugarbayy.configs.security.JwtService;
import com.store.sugarbayy.dtos.TokenResponse;
import com.store.sugarbayy.entities.Admin;
import com.store.sugarbayy.entities.Customer;
import com.store.sugarbayy.entities.RefreshToken;
import com.store.sugarbayy.enums.Role;
import com.store.sugarbayy.repos.AdminRepo;
import com.store.sugarbayy.repos.CustomerRepo;
import com.store.sugarbayy.repos.RefreshTokenRepo;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final EntityManager entityManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final JwtService jwtService;

    @Override
    @Transactional
    public ResponseCookie generate(Long userId, Role role) {
        // delete previous tokens (remove old sessions)
        refreshTokenRepo.deleteByAdminOrCustomerId(userId);
        String secret = Common.generateRandomString(64);
        // build new refresh token entity & save
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenHash(Common.PASSWORD_ENCODER.encode(secret))
                .admin(role == Role.ADMIN ? entityManager.getReference(Admin.class, userId) : null)
                .customer(role == Role.CUSTOMER ? entityManager.getReference(Customer.class, userId) : null)
                .expiry(Instant.now().plus(RefreshTokenHelper.expiryDuration))
                .build();
        refreshTokenRepo.save(refreshTokenEntity);
        // build refreshToken string & create a cookie
        String refreshToken = refreshTokenEntity.getId().toString() + "." + secret;
        return RefreshTokenHelper.createRefreshTokenCookie(refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refresh(HttpServletRequest request) {
        // get token from header
        String token = RefreshTokenHelper.extractRefreshTokenFromCookies(request);
        // validate token
        RefreshToken oldRefreshTokenEntity = validateRefreshTokenAndGetEntity(token);
        // fetch usedId & role
        Long userId;
        Role role;
        if (oldRefreshTokenEntity.getAdmin() != null) {
            userId = oldRefreshTokenEntity.getAdmin().getId();
            role = Role.ADMIN;
        } else {
            userId = oldRefreshTokenEntity.getCustomer().getId();
            role = Role.CUSTOMER;
        }
        // delete previous refreshToken
        refreshTokenRepo.delete(oldRefreshTokenEntity);
        // create new refreshToken and accessToken
        ResponseCookie newRefreshTokenCookie = generate(userId, role);
        String newAccessToken = jwtService.generateToken(userId, role);
        // return
        return new TokenResponse(newRefreshTokenCookie, newAccessToken);
    }

    @Override
    @Transactional
    public ResponseCookie revoke(HttpServletRequest request) {
        try {
            String token = RefreshTokenHelper.extractRefreshTokenFromCookies(request);
            deleteRefreshTokenEntity(token);
        } catch (Exception _) {}
        return RefreshTokenHelper.deleteRefreshTokenCookie();
    }

    // helpers
    private RefreshToken validateRefreshTokenAndGetEntity(String token) {
        if (token == null || token.isBlank()) throw new BadCredentialsException("No refresh token present");
        String[] parts = token.split("\\.");
        if (parts.length != 2) throw new BadCredentialsException("Invalid refresh token");
        long tokenId;
        try {
            tokenId = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        String rawSecret = parts[1];

        return refreshTokenRepo.findById(tokenId)
                .filter(rt -> rt.getExpiry().isAfter(Instant.now()))
                .filter(rt -> Common.PASSWORD_ENCODER.matches(rawSecret, rt.getTokenHash()))
                .filter(rt ->
                        (rt.getAdmin() != null && adminRepo.existsById(rt.getAdmin().getId())) ||
                        (rt.getCustomer() != null && customerRepo.existsById(rt.getCustomer().getId()))
                )
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
    }

    @Transactional
    public void deleteRefreshTokenEntity(String token) {
        if (token == null || token.isBlank()) return;
        String[] parts = token.split("\\.");
        if (parts.length != 2) return;
        long tokenId;
        try {
            tokenId = Long.parseLong(parts[0]);
            refreshTokenRepo.deleteById(tokenId);
        } catch (IllegalArgumentException _) {}
    }
}
