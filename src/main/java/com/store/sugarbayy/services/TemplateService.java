package com.store.sugarbayy.services;

public interface TemplateService {
    String renderOtpEmail(String otp, String expiryMinutes);
    String renderWelcomeEmail(String name);
}
