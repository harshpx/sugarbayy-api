package com.store.sugarbayy.annotations.validEmail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class EmailValidationHelper {
    private final Set<String> DISPOSABLE_DOMAINS;
    private final Pattern EMAIL_REGEX = Pattern
            .compile("^(?!\\.)(?!.*\\.\\.)([a-z0-9_'+.\\-]*[a-z0-9_+\\-])@([a-z0-9][a-z0-9\\-]*\\.)+[a-z]{2,}$");

    public EmailValidationHelper() {
        Set<String> temp = new HashSet<>();
        try {
            InputStream lines = new ClassPathResource("static/blocklisted-email-domains.txt").getInputStream();
            String content = new String(lines.readAllBytes(), StandardCharsets.UTF_8);
            content.lines()
                    .map(s -> s.trim().toLowerCase())
                    .filter(s -> !s.isEmpty())
                    .forEach(temp::add);
            lines.close();
        } catch (Exception _) {
            log.error("Error occurred while creating disposable domain set");
        } finally {
            this.DISPOSABLE_DOMAINS = Collections.unmodifiableSet(temp);
            log.info("Total disposable domains found: {}", this.DISPOSABLE_DOMAINS.size());
        }
    }

    public boolean isValidEmail(String email) {
        // check null or empty
        if (email == null || email.isBlank()) return false;
        // check email format
        if (!this.EMAIL_REGEX.matcher(email).matches()) return false;
        String[] emailParts = email.split("@", 2);
        // check domain validity
        String domain = emailParts[1];
        return !this.DISPOSABLE_DOMAINS.contains(domain.toLowerCase());
    }
}
