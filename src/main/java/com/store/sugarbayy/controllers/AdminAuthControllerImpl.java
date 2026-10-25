package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.*;
import com.store.sugarbayy.services.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/admin")
public class AdminAuthControllerImpl implements AdminAuthController {
    private final AdminAuthService adminAuthService;

    @Override
    @PostMapping("/login/credentials")
    public ResponseEntity<SuccessResponse<AccessTokenResponse>> adminCredentialLogin(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse response = adminAuthService.adminCredentialLogin(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.refreshTokenCookie().toString())
                .body(SuccessResponse.of(AccessTokenResponse.of(response.accessToken())));
    }

    @Override
    @PostMapping("/login/email-otp/send")
    public ResponseEntity<SuccessResponse<String>> adminEmailSendOtp(@Valid @RequestBody EmailDto emailDto) {
        adminAuthService.sendAdminEmailLoginOtp(emailDto.email());
        return ResponseEntity.ok(SuccessResponse.of("Login OTP sent on email"));
    }

    @Override
    @PostMapping("/login/email-otp")
    public ResponseEntity<SuccessResponse<AccessTokenResponse>> adminEmailOtpLogin(@Valid @RequestBody EmailOtpRequest emailOtpRequest) {
        TokenResponse response = adminAuthService.adminEmailOtpLogin(emailOtpRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.refreshTokenCookie().toString())
                .body(SuccessResponse.of(AccessTokenResponse.of(response.accessToken())));
    }
}
