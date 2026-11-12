package com.store.sugarbayy.configurations.security;

import com.store.sugarbayy.configurations.Common;
import com.store.sugarbayy.configurations.security.jwt.JwtFilter;
import com.store.sugarbayy.dtos.common.ErrorResponse;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtFilter jwtFilter;
    private final UserDetailsService adminUserDetailsService;
    private final UserDetailsService customerUserDetailsService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final ObjectMapper objectMapper;

    public SecurityConfig(
            CorsConfigurationSource corsConfigurationSource,
            JwtFilter jwtFilter,
            @Qualifier("adminUserDetailsService") UserDetailsService adminUserDetailsService,
            @Qualifier("customerUserDetailsService") UserDetailsService customerUserDetailsService,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper objectMapper
    ) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtFilter = jwtFilter;
        this.adminUserDetailsService = adminUserDetailsService;
        this.customerUserDetailsService = customerUserDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) {
        return https
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(req -> req
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        .requestMatchers(Common.PROTECTED_RESOURCES).hasRole("ADMIN")
                        .anyRequest().permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(send -> send.baseUri("/auth/customer/login/oauth"))
                        .redirectionEndpoint(redirect -> redirect.baseUri("/login/oauth2/code/*"))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((_, res, _) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write(
                                    objectMapper.writeValueAsString(new ErrorResponse("Unauthorized"))
                            );
                        })
                )
                .build();
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(adminUserDetailsService);
        authenticationProvider.setPasswordEncoder(Common.PASSWORD_ENCODER);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationProvider customerAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(customerUserDetailsService);
        authenticationProvider.setPasswordEncoder(Common.PASSWORD_ENCODER);
        return authenticationProvider;
    }

    @Bean
    @Primary
    public AuthenticationManager adminAuthenticationManager() {
        return new ProviderManager(adminAuthenticationProvider());
    }

    //    @Bean
    //    public AuthenticationManager customerAuthenticationManager() {
    //        return new ProviderManager(customerAuthenticationProvider());
    //    }
}
