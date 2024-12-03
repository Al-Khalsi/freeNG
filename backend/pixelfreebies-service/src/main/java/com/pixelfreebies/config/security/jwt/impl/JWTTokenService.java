package com.pixelfreebies.config.security.jwt.impl;

import com.pixelfreebies.config.security.jwt.TokenClaimsBuilder;
import com.pixelfreebies.config.security.jwt.TokenService;
import com.pixelfreebies.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static com.pixelfreebies.util.constants.ApplicationConstants.JWT_DEFAULT_VALUE;
import static com.pixelfreebies.util.constants.ApplicationConstants.JWT_SECRET_KEY;

@Component
@RequiredArgsConstructor
public class JWTTokenService implements TokenService {

    private final Environment env;
    private final TokenClaimsBuilder claimsBuilder;
    private @Value("${spring.application.name}") String applicationName;
    private @Value("${token.jwt.expires_in}") long expiresIn;

    @Override
    public SecretKey getSecretKey() {
        if (this.env == null) throw new NotFoundException("Environment not set");
        String secret = this.env.getProperty(JWT_SECRET_KEY, JWT_DEFAULT_VALUE);

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = this.claimsBuilder.buildClaims(authentication);

        return Jwts.builder()
                .issuer(this.applicationName)
                .subject(authentication.getName())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + this.expiresIn * 60_000))
                .signWith(this.getSecretKey())
                .compact();
    }

    @Override
    public Map<String, Object> validateTokenAndExtractClaims(String token) {
        Claims claims = Jwts.parser().verifyWith(this.getSecretKey()).build()
                .parseSignedClaims(token).getPayload();

        return Map.of(
                "email", claims.get("email", String.class),
                "username", claims.get("username", String.class),
                "role", claims.get("role", String.class)
        );
    }

}
