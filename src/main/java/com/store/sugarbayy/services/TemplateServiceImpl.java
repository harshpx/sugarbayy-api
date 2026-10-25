package com.store.sugarbayy.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final TemplateEngine templateEngine;

    @Override
    public String renderOtpEmail(String otp, String expiryMinutes) {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", expiryMinutes);
        return templateEngine.process("email-otp", context);
    }

    @Override
    public String renderWelcomeEmail(String name) {
        Context context = new Context();
        context.setVariable("name", name);
        return templateEngine.process("welcome", context);
    }
}
