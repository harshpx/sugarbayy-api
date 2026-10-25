package com.store.sugarbayy.services;

import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.LoginRequest;
import com.store.sugarbayy.dtos.TokenResponse;

public interface AdminAuthService {
    TokenResponse adminCredentialLogin(LoginRequest loginRequest);
    void sendAdminEmailLoginOtp(String email);
    TokenResponse adminEmailOtpLogin(EmailOtpRequest emailOtpRequest);
}
