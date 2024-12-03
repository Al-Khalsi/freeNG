package com.pixelfreebies.config.security.jwt;

import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Map;

public interface TokenService {

    String generateToken(Authentication authentication);

    Map<String, Object> validateTokenAndExtractClaims(String token);

    SecretKey getSecretKey();

}
