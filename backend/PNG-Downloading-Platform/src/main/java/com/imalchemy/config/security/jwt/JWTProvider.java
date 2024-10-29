package com.imalchemy.config.security.jwt;

import com.imalchemy.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.imalchemy.util.constants.ApplicationConstants.JWT_DEFAULT_VALUE;
import static com.imalchemy.util.constants.ApplicationConstants.JWT_SECRET_KEY;

@Component
@RequiredArgsConstructor
public class JWTProvider {

    private @Value("${spring.application.name}") String applicationName;
    private @Value("${token.jwt.expires_in}") long expiresIn;

    private final Environment env;

    public SecretKey extractSecretKey() {
        if (this.env == null) {
            throw new NotFoundException("Environment not set");
        }
        String secret = this.env.getProperty(JWT_SECRET_KEY, JWT_DEFAULT_VALUE);
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication) {
        return Jwts.builder()
                .issuer(this.applicationName)
                .subject(authentication.getName())
                .claim("email", authentication.getName())
                .claim("authorities", authentication.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")) // separate authorities with delimiter comma
                ).issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + this.expiresIn * 60_000)) // convert minutes to milliseconds
                .signWith(this.extractSecretKey())
                .compact();
    }

    public Map<String, Object> extractClaims(String token, SecretKey secretKey) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build() // jwt token validation
                .parseSignedClaims(token).getPayload();// fetch user details

        String username = String.valueOf(claims.get("email"));
        String authorities = String.valueOf(claims.get("authorities")); // comma separated authorities

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        return Map.of("email", username, "authorities", grantedAuthorities);
    }

}
