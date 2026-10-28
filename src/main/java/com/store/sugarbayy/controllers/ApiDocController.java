package com.store.sugarbayy.controllers;

import com.store.sugarbayy.annotations.validEmail.ValidEmail;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Size;
import org.springframework.ui.Model;

public interface ApiDocController {
    String loginPage(String error, Model model, HttpServletResponse response);
    String sendOtp(String email, Model model, HttpServletResponse response);
    String emailLogin(@ValidEmail String email, @Size(min = 6, max = 6) String otp, Model model, HttpServletResponse response);
    String credentialLogin(@ValidEmail String email, String password, Model model, HttpServletResponse response);
}
