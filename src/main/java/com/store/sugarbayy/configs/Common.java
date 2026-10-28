package com.store.sugarbayy.configs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Common {
    public static final String[] ADMIN_ENDPOINTS = {"/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml"};
    public static final String[] PUBLIC_ENDPOINTS = {"/", "/auth/**", "/swagger-ui.html", "/swagger-ui/**"};
    public static final String DELIMITER = "\u001F";

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(10);
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final SecureRandom random = new SecureRandom();

    public static boolean isPublicEndpoint(String endpoint) {
        for (String pattern : PUBLIC_ENDPOINTS) {
            if (PATH_MATCHER.match(pattern, endpoint)) return true;
        }
        return false;
    }

    public static String generateRandomString(int length) {
        final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateRandomNumericString(int length) {
        final String NUMS = "0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMS.charAt(random.nextInt(NUMS.length())));
        }
        return sb.toString();
    }

    public static String encodeBase64(String originalString) {
        return Base64.getEncoder().encodeToString(originalString.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
