package com.store.sugarbayy.configs.security.oauth;

import com.store.sugarbayy.configs.ApiException;
import com.store.sugarbayy.dtos.ErrorResponse;
import com.store.sugarbayy.services.CustomerAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class OAuthConfig {
    private final String clientUrl;
    private final CustomerAuthService customerAuthService;

    @Autowired
    public OAuthConfig(
            @Value("${oauth.redirect-origin}") String clientUrl,
            CustomerAuthService customerAuthService
    ) {
        this.clientUrl = clientUrl;
        this.customerAuthService = customerAuthService;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (_, response, exception) -> {
            log.info("OAuth2 Error", exception);
            ObjectMapper objectMapper = new ObjectMapper();
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter()
                    .write(objectMapper.writeValueAsString(new ErrorResponse("OAuth2 Authentication Failed")));
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (_, response, authentication) -> {
            OAuth2AuthenticationToken oAuthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oAuthToken.getPrincipal();
            String provider = oAuthToken.getAuthorizedClientRegistrationId();
            OAuthUserInfo userInfo = getUserInfo(oAuth2User, provider);
            try {
                ResponseCookie refreshTokenCookie = customerAuthService.customerOauthLoginHelper(userInfo);
                response.setHeader("Set-Cookie", refreshTokenCookie.toString());
                response.sendRedirect(clientUrl + "/oauth2redirect");
            } catch (Exception ex) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String error = "Oauth2 Authentication Failed";
                String urlEncodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
                response.sendRedirect(clientUrl + "/oauth2redirect?error=" + urlEncodedError);
            }
        };
    }

    // helpers
    private OAuthUserInfo getUserInfo(OAuth2User oAuthUser, String provider) {
        if (provider.equalsIgnoreCase("google")) {
            return new OAuthUserInfoGoogle(oAuthUser);
        }
        throw new ApiException("Unsupported OAuth2 provider", HttpStatus.UNAUTHORIZED);
    }
}
