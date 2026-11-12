package com.store.sugarbayy.controllers.auth;

import com.store.sugarbayy.dtos.auth.AccessTokenResponse;
import com.store.sugarbayy.dtos.auth.EmailInfo;
import com.store.sugarbayy.dtos.auth.EmailOtpRequest;
import com.store.sugarbayy.dtos.auth.TokenResponse;
import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.services.auth.CustomerAuthService;
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
@RequestMapping("/auth/customer")
public class CustomerAuthControllerImpl implements CustomerAuthController {
    private final CustomerAuthService customerAuthService;

    @Override
    @PostMapping("/login/email-otp/send")
    public ResponseEntity<SuccessResponse<String>> customerEmailSendOtp(@Valid @RequestBody EmailInfo emailInfo) {
        customerAuthService.sendCustomerEmailLoginOtp(emailInfo.email());
        return ResponseEntity.ok(SuccessResponse.of("Login OTP sent on email"));
    }

    @Override
    @PostMapping("/login/email-otp")
    public ResponseEntity<SuccessResponse<AccessTokenResponse>> customerEmailOtpLogin(@Valid @RequestBody EmailOtpRequest emailOtpRequest) {
        TokenResponse response = customerAuthService.customerEmailOtpLogin(emailOtpRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.refreshTokenCookie().toString())
                .body(SuccessResponse.of(AccessTokenResponse.of(response.accessToken())));
    }
}
