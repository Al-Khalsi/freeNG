package com.pixelfreebies.config.security;

import com.pixelfreebies.config.security.auth.CustomAuthenticationProviderProd;
import com.pixelfreebies.config.security.entrypoint.CustomBasicAuthenticationEntryPoint;
import com.pixelfreebies.config.security.entrypoint.CustomBearerTokenAccessDeniedHandler;
import com.pixelfreebies.config.security.entrypoint.CustomBearerTokenAuthenticationEntryPoint;
import com.pixelfreebies.config.security.filter.JWTTokenGeneratorFilter;
import com.pixelfreebies.config.security.filter.JWTTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("prod")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigProd extends BaseSecurityConfig {

    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;
    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;

    private @Value("${cors.origin.url.permitted}") String permittedCorsOriginUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        this.configureCors(http, this.permittedCorsOriginUrl);
        this.configureCommonSecurity(http, this.jwtTokenValidatorFilter, this.jwtTokenGeneratorFilter, this.customBasicAuthenticationEntryPoint, this.customBearerTokenAuthenticationEntryPoint, this.customBearerTokenAccessDeniedHandler);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(this.permittedUrls).permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        CustomAuthenticationProviderProd authenticationProvider = new CustomAuthenticationProviderProd(userDetailsService, passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

}