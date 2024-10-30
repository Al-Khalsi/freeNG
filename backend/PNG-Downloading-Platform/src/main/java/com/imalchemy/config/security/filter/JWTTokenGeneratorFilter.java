package com.imalchemy.config.security.filter;

import com.imalchemy.config.security.jwt.JWTProvider;
import com.imalchemy.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.imalchemy.util.constants.ApplicationConstants.JWT_AUTHORIZATION_HEADER;

@Component
@RequiredArgsConstructor
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final SecurityUtil securityUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String jwt = this.jwtProvider.createToken(authentication);
            response.setHeader(JWT_AUTHORIZATION_HEADER, jwt);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // return true if any excluding path matches
        return this.securityUtil.getPERMITTED_URLS().stream().anyMatch(path::startsWith);
    }

}
