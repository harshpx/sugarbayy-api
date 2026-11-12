package com.store.sugarbayy.controllers.auth;

import com.store.sugarbayy.dtos.auth.AccessTokenResponse;
import com.store.sugarbayy.dtos.auth.EmailInfo;
import com.store.sugarbayy.dtos.auth.EmailOtpRequest;
import com.store.sugarbayy.dtos.common.SuccessResponse;
import org.springframework.http.ResponseEntity;

public interface CustomerAuthController {
    ResponseEntity<SuccessResponse<String>> customerEmailSendOtp(EmailInfo emailInfo);
    ResponseEntity<SuccessResponse<AccessTokenResponse>> customerEmailOtpLogin(EmailOtpRequest emailOtpRequest);
}
