package com.imalchemy.config.security.filter;

import com.imalchemy.config.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.imalchemy.util.constants.ApplicationConstants.JWT_AUTHORIZATION_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // read the authorization header
        String jwt = request.getHeader(JWT_AUTHORIZATION_HEADER);
        if (jwt != null) {
            try {
                Map<String, Object> claimsMap = this.jwtProvider.extractClaims(jwt, this.jwtProvider.extractSecretKey());
                String email = String.valueOf(claimsMap.get("email"));
                String authorities = String.valueOf(claimsMap.get("authorities"));

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                throw new BadCredentialsException("Invalid JWT token received");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldNotFilter = EXCLUDED_PATHS.stream()
                .anyMatch(path::equals);

        System.out.println("Path: " + path + ", Should not filter: " + shouldNotFilter);
        return shouldNotFilter;
    }
}
