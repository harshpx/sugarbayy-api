package com.store.sugarbayy.services;

import com.store.sugarbayy.enums.VerificationTokenType;

public interface VerificationTokenService {
    void sendEmailVerificationOtp(String email);
    boolean verifyOtp(String identifier, String otp, VerificationTokenType type);
}
