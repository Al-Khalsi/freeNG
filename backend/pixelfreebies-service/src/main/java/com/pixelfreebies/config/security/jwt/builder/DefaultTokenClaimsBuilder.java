package com.pixelfreebies.config.security.jwt.builder;

import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultTokenClaimsBuilder implements TokenClaimsBuilder {

    private final UserRepository userRepository;

    @Override
    public Map<String, Object> buildClaims(Authentication authentication) {
        String email;
        if (authentication.getPrincipal() instanceof UserDetails userDetails)
            email = userDetails.getUsername();
        else email = authentication.getPrincipal().toString();

        // Fetch additional user details
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return Map.of(
                "email", email,
                "username", user.getUsername(),
                "role", authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","))
        );
    }

}
