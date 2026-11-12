package com.store.sugarbayy.services.auth;

import com.store.sugarbayy.configurations.security.oauth.OAuthUserInfo;
import com.store.sugarbayy.dtos.auth.EmailOtpRequest;
import com.store.sugarbayy.dtos.auth.TokenResponse;
import org.springframework.http.ResponseCookie;

public interface CustomerAuthService {
    void sendCustomerEmailLoginOtp(String email);
    TokenResponse customerEmailOtpLogin(EmailOtpRequest emailOtpRequest);
    ResponseCookie customerOauthLoginHelper(OAuthUserInfo oAuthUserInfo);
}
