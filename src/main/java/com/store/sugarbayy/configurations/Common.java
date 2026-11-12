package com.store.sugarbayy.configurations;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

public class Common {
    public static final String[] PROTECTED_RESOURCES = {"/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml"};

//    public static final String DELIMITER = "\u001F";

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(12);

    private static final SecureRandom random = new SecureRandom();

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

    public static String toSentenceCase(String str) {
        if (str == null) return null;
        String[] words = str.trim().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                sb.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            } else {
                sb.append(word.toLowerCase()).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
