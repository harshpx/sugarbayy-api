package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.LoginRequest;
import com.store.sugarbayy.services.AdminAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth/admin/apidoc/login")
@RequiredArgsConstructor
public class ApiDocControllerImpl implements ApiDocController {
    private final AdminAuthService adminAuthService;

    @Override
    @GetMapping({"", "/"})
    public String loginPage(
            @RequestParam(required = false) String error,
            Model model,
            HttpServletResponse response
    ) {
        noStore(response);
        model.addAttribute("error", error);
        return "apidoc-login";
    }

    @Override
    @PostMapping("/email/send-otp")
    public String sendOtp(
            @RequestParam String email,
            Model model,
            HttpServletResponse response
    ) {
        noStore(response);
         try {
             adminAuthService.sendAdminEmailLoginOtp(email.trim());
             model.addAttribute("email", email.trim());
             model.addAttribute("sent", true);
         } catch (Exception e) {
             model.addAttribute("error", e.getMessage());
         }
         return "apidoc-login";
    }

    @Override
    @PostMapping("/email")
    public String emailLogin(
            @RequestParam String email,
            @RequestParam String otp,
            Model model,
            HttpServletResponse response
) {
        noStore(response);
        try {
            String token = adminAuthService.adminEmailOtpLoginNoRefresh(new EmailOtpRequest(email.trim(), otp.trim()));
            model.addAttribute("accessToken", token);
            model.addAttribute("email", email.trim());
            return "apidoc-swagger";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "apidoc-login";
        }
    }

    @Override
    @PostMapping("/credentials")
    public String credentialLogin(String email, String password, Model model, HttpServletResponse response) {
        noStore(response);
        try {
            String token = adminAuthService.adminCredentialLoginNoRefresh(new LoginRequest(email.trim(), password));
            model.addAttribute("accessToken", token);
            model.addAttribute("email", email.trim());
            return "apidoc-swagger";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "apidoc-login";
        }
    }

    // helpers
    private void noStore(HttpServletResponse response) {
        response.setHeader(
                "Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
    }
}
