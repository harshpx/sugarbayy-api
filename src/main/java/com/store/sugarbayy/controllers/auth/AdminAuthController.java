package com.store.sugarbayy.controllers.auth;

import com.store.sugarbayy.dtos.auth.AccessTokenResponse;
import com.store.sugarbayy.dtos.auth.EmailInfo;
import com.store.sugarbayy.dtos.auth.EmailOtpRequest;
import com.store.sugarbayy.dtos.auth.LoginRequest;
import com.store.sugarbayy.dtos.common.SuccessResponse;
import org.springframework.http.ResponseEntity;

public interface AdminAuthController {
    ResponseEntity<SuccessResponse<AccessTokenResponse>> adminCredentialLogin(LoginRequest loginRequest);
    ResponseEntity<SuccessResponse<String>> adminEmailSendOtp(EmailInfo emailInfo);
    ResponseEntity<SuccessResponse<AccessTokenResponse>> adminEmailOtpLogin(EmailOtpRequest emailOtpRequest);
}
