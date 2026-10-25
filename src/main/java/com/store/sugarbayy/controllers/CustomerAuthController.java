package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.AccessTokenResponse;
import com.store.sugarbayy.dtos.EmailDto;
import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.SuccessResponse;
import org.springframework.http.ResponseEntity;

public interface CustomerAuthController {
    ResponseEntity<SuccessResponse<String>> customerEmailSendOtp(EmailDto emailDto);
    ResponseEntity<SuccessResponse<AccessTokenResponse>> customerEmailOtpLogin(EmailOtpRequest emailOtpRequest);
}
