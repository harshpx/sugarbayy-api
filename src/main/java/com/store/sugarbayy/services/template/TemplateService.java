package com.store.sugarbayy.services.template;

public interface TemplateService {
    String renderOtpEmail(String otp, String expiryMinutes);
    String renderWelcomeEmail(String name);
}
