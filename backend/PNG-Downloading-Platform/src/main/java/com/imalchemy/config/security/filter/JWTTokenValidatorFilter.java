package com.imalchemy.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imalchemy.config.security.jwt.JWTProvider;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.util.SecurityUtil;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.imalchemy.util.constants.ApplicationConstants.JWT_AUTHORIZATION_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final SecurityUtil securityUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // read the authorization header
        String jwt = request.getHeader(JWT_AUTHORIZATION_HEADER);
        if (jwt != null) {
            try {
                jwt = jwt.substring("Bearer ".length());
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
                if (e instanceof SignatureException signatureInvalidException) {
                    handleInvalidBearerTokenException(response, signatureInvalidException);
                    return;
                }
                throw new BadCredentialsException("Invalid JWT token received");
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidBearerTokenException(HttpServletResponse response, SignatureException e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Result result = new Result(false, HttpStatus.UNAUTHORIZED, "The access token provided is expired, revoked, malformed, or invalid for other reasons.", e.getMessage());

        String jsonResult = this.objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResult);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // return true if any excluding path matches
        return this.securityUtil.getPERMITTED_URLS().stream().anyMatch(path::startsWith);
    }

}
