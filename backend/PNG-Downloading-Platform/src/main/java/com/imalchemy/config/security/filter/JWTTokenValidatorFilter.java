package com.imalchemy.config.security.filter;

import com.imalchemy.config.security.jwt.JWTProvider;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private @Value("${base.url}") String baseUrl;
    private String EXCLUDED_PATH = "";

    @PostConstruct
    public void init() {
        this.EXCLUDED_PATH = this.baseUrl + "/auth";
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
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
        return path.startsWith(this.EXCLUDED_PATH);
    }

}
