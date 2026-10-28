package com.store.sugarbayy.services;

import com.store.sugarbayy.configs.ApiException;
import com.store.sugarbayy.configs.security.JwtService;
import com.store.sugarbayy.dtos.EmailOtpRequest;
import com.store.sugarbayy.dtos.LoginRequest;
import com.store.sugarbayy.dtos.TokenResponse;
import com.store.sugarbayy.entities.Admin;
import com.store.sugarbayy.enums.Role;
import com.store.sugarbayy.enums.VerificationTokenType;
import com.store.sugarbayy.repos.AdminRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final AdminRepo adminRepo;
    private final VerificationTokenService verificationTokenService;
    private final JwtService jwtService;

    // send otp to email
    @Override
    public void sendAdminEmailLoginOtp(String email) {
        if (!adminRepo.existsByEmail(email)) {
            throw new ApiException("This email is not registered as Sugarbayy admin", HttpStatus.UNAUTHORIZED);
        }
        verificationTokenService.sendEmailVerificationOtp(email);
    }

    // email - otp login
    @Override
    public TokenResponse adminEmailOtpLogin(EmailOtpRequest emailOtpRequest) {
        Long adminId = adminRepo.findIdByEmail(emailOtpRequest.email())
                .orElseThrow(() -> new ApiException(
                        "This email is not registered as Sugarbayy admin",
                        HttpStatus.UNAUTHORIZED)
                );
        if (!verificationTokenService.verifyOtp(
                emailOtpRequest.email(),
                emailOtpRequest.otp(),
                VerificationTokenType.VERIFY_EMAIL
        )) {
            throw new BadCredentialsException("Invalid / Expired OTP");
        }
        ResponseCookie refreshTokenCookie = refreshTokenService.generate(adminId, Role.ADMIN);
        String accessToken = jwtService.generateToken(adminId, Role.ADMIN);
        return new TokenResponse(refreshTokenCookie, accessToken);
    }

    // email - otp, no refresh token
    @Override
    public String adminEmailOtpLoginNoRefresh(EmailOtpRequest emailOtpRequest) {
        Long adminId = adminRepo.findIdByEmail(emailOtpRequest.email())
                .orElseThrow(() -> new ApiException(
                        "This email is not registered as Sugarbayy admin",
                        HttpStatus.UNAUTHORIZED)
                );
        if (!verificationTokenService.verifyOtp(
                emailOtpRequest.email(),
                emailOtpRequest.otp(),
                VerificationTokenType.VERIFY_EMAIL
        )) {
            throw new BadCredentialsException("Invalid / Expired OTP");
        }
        return jwtService.generateToken(adminId, Role.ADMIN);
    }

    // identifier - password login
    @Override
    public TokenResponse adminCredentialLogin(LoginRequest loginRequest) {
        String identifier = loginRequest.identifier();
        String password = loginRequest.password();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(identifier, password));
        Admin admin = (Admin) authentication.getPrincipal();
        assert admin != null;
        ResponseCookie refreshTokenCookie = refreshTokenService.generate(admin.getId(), Role.ADMIN);
        String accessToken = jwtService.generateToken(admin.getId(), Role.ADMIN);
        return new TokenResponse(refreshTokenCookie, accessToken);
    }

    // identifier - password login, no refresh token
    @Override
    public String adminCredentialLoginNoRefresh(LoginRequest loginRequest) {
        String identifier = loginRequest.identifier();
        String password = loginRequest.password();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(identifier, password));
        Admin admin = (Admin) authentication.getPrincipal();
        assert admin != null;
        return jwtService.generateToken(admin.getId(), Role.ADMIN);
    }
}
