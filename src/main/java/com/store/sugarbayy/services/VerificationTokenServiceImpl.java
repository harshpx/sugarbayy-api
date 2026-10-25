package com.store.sugarbayy.services;

import com.store.sugarbayy.configs.ApiException;
import com.store.sugarbayy.configs.Common;
import com.store.sugarbayy.entities.VerificationToken;
import com.store.sugarbayy.enums.VerificationTokenChannel;
import com.store.sugarbayy.enums.VerificationTokenType;
import com.store.sugarbayy.repos.VerificationTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepo verificationTokenRepo;
    private final EmailService emailService;
    private final TemplateService templateService;

    private final int expiryMinutes = 10;

    @Override
    @Transactional
    public void sendEmailVerificationOtp(String email) {
        // check if a non-expired token already exists
        if (verificationTokenRepo.existsValidToken(email, VerificationTokenType.VERIFY_EMAIL)) {
            throw new ApiException("Verification code already sent to this email, try again after " + expiryMinutes + " minutes", HttpStatus.CONFLICT);
        }
        // generate otp and save to db
        String otp = Common.generateRandomNumericString(6);
        String otpHash = Common.PASSWORD_ENCODER.encode(otp);
        VerificationToken verificationToken = VerificationToken.builder()
                .codeHash(otpHash)
                .identifier(email)
                .channel(VerificationTokenChannel.EMAIL)
                .type(VerificationTokenType.VERIFY_EMAIL)
                .expiry(Instant.now().plus(Duration.ofMinutes(expiryMinutes)))
                .build();
        verificationTokenRepo.save(verificationToken);
        // create & send email
        String otpEmailContentHTML = templateService.renderOtpEmail(otp, String.valueOf(expiryMinutes));
        emailService.sendEmail(email, "SugarBayy Login OTP", otpEmailContentHTML, true);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String identifier, String otp, VerificationTokenType type) {
        VerificationToken token = verificationTokenRepo
                .findValidToken(identifier, type, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);
        if (token == null) {return false;}
        if (!Common.PASSWORD_ENCODER.matches(otp, token.getCodeHash())) {return false;}
        token.setManualRevoke(true);
        verificationTokenRepo.save(token);
        return true;
    }
}
