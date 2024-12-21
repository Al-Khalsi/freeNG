package com.pixelfreebies.config.security.jwt.builder;

import org.springframework.security.core.Authentication;

import java.util.Map;

public interface TokenClaimsBuilder {

    Map<String, Object> buildClaims(Authentication authentication);

}
