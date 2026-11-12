package com.store.sugarbayy.services.auth;

import com.store.sugarbayy.dtos.auth.EmailOtpRequest;
import com.store.sugarbayy.dtos.auth.LoginRequest;
import com.store.sugarbayy.dtos.auth.TokenResponse;

public interface AdminAuthService {
    void sendAdminEmailLoginOtp(String email);
    TokenResponse adminEmailOtpLogin(EmailOtpRequest emailOtpRequest);
    String adminEmailOtpLoginNoRefresh(EmailOtpRequest emailOtpRequest);
    TokenResponse adminCredentialLogin(LoginRequest loginRequest);
    String adminCredentialLoginNoRefresh(LoginRequest loginRequest);
}
