package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.AccessTokenResponse;
import com.store.sugarbayy.dtos.SuccessResponse;
import com.store.sugarbayy.dtos.TokenResponse;
import com.store.sugarbayy.services.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class CommonAuthControllerImpl implements CommonAuthController {
    private final RefreshTokenService refreshTokenService;

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<AccessTokenResponse>> refresh(HttpServletRequest request) {
        TokenResponse response = refreshTokenService.refresh(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.refreshTokenCookie().toString())
                .body(SuccessResponse.of(AccessTokenResponse.of(response.accessToken())));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Null>> logout(HttpServletRequest request) {
        ResponseCookie deleteCookie = refreshTokenService.revoke(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(SuccessResponse.of(null));
    }
}
