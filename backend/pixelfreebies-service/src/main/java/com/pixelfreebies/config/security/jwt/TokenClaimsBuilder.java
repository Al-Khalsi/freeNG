package com.pixelfreebies.config.security.jwt;

import org.springframework.security.core.Authentication;

import java.util.Map;

public interface TokenClaimsBuilder {

    Map<String, Object> buildClaims(Authentication authentication);

}
