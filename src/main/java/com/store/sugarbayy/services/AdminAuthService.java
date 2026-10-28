package com.store.sugarbayy.services;

import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.LoginRequest;
import com.store.sugarbayy.dtos.TokenResponse;

public interface AdminAuthService {
    void sendAdminEmailLoginOtp(String email);
    TokenResponse adminEmailOtpLogin(EmailOtpRequest emailOtpRequest);
    String adminEmailOtpLoginNoRefresh(EmailOtpRequest emailOtpRequest);
    TokenResponse adminCredentialLogin(LoginRequest loginRequest);
    String adminCredentialLoginNoRefresh(LoginRequest loginRequest);
}
