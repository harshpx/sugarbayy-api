package com.store.sugarbayy.services;

import com.store.sugarbayy.configs.security.oauth.OAuthUserInfo;
import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.TokenResponse;
import org.springframework.http.ResponseCookie;

public interface CustomerAuthService {
    void sendCustomerEmailLoginOtp(String email);
    TokenResponse customerEmailOtpLogin(EmailOtpRequest emailOtpRequest);
    ResponseCookie customerOauthLoginHelper(OAuthUserInfo oAuthUserInfo);
}
