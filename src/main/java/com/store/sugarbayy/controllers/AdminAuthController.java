package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.*;
import org.springframework.http.ResponseEntity;

public interface AdminAuthController {
    ResponseEntity<SuccessResponse<AccessTokenResponse>> adminCredentialLogin(LoginRequest loginRequest);
    ResponseEntity<SuccessResponse<String>> adminEmailSendOtp(EmailDto emailDto);
    ResponseEntity<SuccessResponse<AccessTokenResponse>> adminEmailOtpLogin(EmailOtpRequest emailOtpRequest);
//    ResponseEntity<SuccessResponse<String>> adminContactSendOtp(ContactNumberDto contactNumberDto);
//    ResponseEntity<SuccessResponse<AuthResponse>> adminContactOtpLogin(ContactNumberOtpRequest contactNumberOtpRequest);
}
