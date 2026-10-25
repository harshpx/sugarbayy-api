package com.store.sugarbayy.configs.security;

import com.store.sugarbayy.configs.Common;
import com.store.sugarbayy.dtos.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            // check public endpoint
            String requestEndpoint = request.getRequestURI();
            if (Common.isPublicEndpoint(requestEndpoint)) {
                filterChain.doFilter(request, response);
                return;
            }

            // check auth header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ServletException("Authorization header missing");
            }

            // extract claims
            String token = authHeader.substring("Bearer ".length());
            Claims claims = jwtService.extractClaims(token);

            // set authentication context
            AuthPrincipal principal = new AuthPrincipal(claims.getSubject(), claims.get("role", String.class));
            Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_" + principal.role().name()));
            var authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // forward filter
            filterChain.doFilter(request, response);

        } catch (JwtException | ServletException | IllegalArgumentException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter()
                    .write(objectMapper.writeValueAsString(new ErrorResponse("Invalid or expired token, Authorization failed")));
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter()
                    .write(objectMapper.writeValueAsString(new ErrorResponse("An unknown error has occurred.")));
        }
    }
}
