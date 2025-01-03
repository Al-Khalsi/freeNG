package com.pixelfreebies.config.security.filter;

import com.pixelfreebies.config.security.jwt.TokenService;
import com.pixelfreebies.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.pixelfreebies.util.constants.ApplicationConstants.JWT_AUTHORIZATION_HEADER;

@Component
@RequiredArgsConstructor
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private final SecurityUtil securityUtil;
    private final TokenService tokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // return true if any excluding path matches
        return this.securityUtil.getPERMITTED_URLS().stream()
                .anyMatch(request.getServletPath()::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .ifPresent(authentication -> {
                    String jwt = this.tokenService.generateToken(authentication);
                    response.setHeader(JWT_AUTHORIZATION_HEADER, jwt);
                });

        filterChain.doFilter(request, response);
    }

}
