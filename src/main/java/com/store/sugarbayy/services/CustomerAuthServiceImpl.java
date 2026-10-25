package com.store.sugarbayy.services;

import com.store.sugarbayy.configs.security.JwtService;
import com.store.sugarbayy.configs.security.oauth.OAuthUserInfo;
import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.TokenResponse;
import com.store.sugarbayy.entities.Customer;
import com.store.sugarbayy.enums.Role;
import com.store.sugarbayy.enums.VerificationTokenType;
import com.store.sugarbayy.repos.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImpl implements CustomerAuthService {
    private final CustomerRepo customerRepo;
    private final VerificationTokenService verificationTokenService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final TemplateService templateService;

    @Override
    @Transactional
    public void sendCustomerEmailLoginOtp(String email) {
        verificationTokenService.sendEmailVerificationOtp(email);
    }

    @Override
    @Transactional
    public TokenResponse customerEmailOtpLogin(EmailOtpRequest emailOtpRequest) {
        if (!verificationTokenService.verifyOtp(
                emailOtpRequest.email(),
                emailOtpRequest.otp(),
                VerificationTokenType.VERIFY_EMAIL
        )) {
            throw new BadCredentialsException("Invalid code");
        }

        Customer customer = customerRepo.findByEmail(emailOtpRequest.email()).orElse(null);

        if (customer == null) {
            customer = Customer.builder().email(emailOtpRequest.email()).build();
            customer = customerRepo.save(customer);
            sendWelcomeEmailToNewCustomers(customer.getEmail());
        }

        ResponseCookie refreshTokenCookie = refreshTokenService.generate(customer.getId(), Role.CUSTOMER);
        String accessToken = jwtService.generateToken(customer.getId(), Role.CUSTOMER);
        return TokenResponse.of(refreshTokenCookie, accessToken);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    // only to be used for OAuth (insecure method, directly creates customer without validation)
    public ResponseCookie customerOauthLoginHelper(OAuthUserInfo oAuthUserInfo) {
        Customer customer = customerRepo.findByEmail(oAuthUserInfo.email()).orElse(null);
        if (customer == null) {
            customer = Customer.builder()
                    .email(oAuthUserInfo.email())
                    .name(oAuthUserInfo.name())
                    .build();
            customer = customerRepo.save(customer);
            sendWelcomeEmailToNewCustomers(customer.getEmail());
        }
        return refreshTokenService.generate(customer.getId(), Role.CUSTOMER);
    }


    // helpers
    private void sendWelcomeEmailToNewCustomers(String email) {
        String[] emailParts = email.split("@");
        String username = emailParts[0] != null && !emailParts[0].isBlank() ? emailParts[0] : "Customer";
        String welcomeEmailContentHTML = templateService.renderWelcomeEmail(username);
        emailService.sendEmail(email, "Welcome to SugarBayy!", welcomeEmailContentHTML, false);
    }
}
