package com.pixelfreebies.config.security;

import com.pixelfreebies.config.security.entrypoint.CustomBasicAuthenticationEntryPoint;
import com.pixelfreebies.config.security.entrypoint.CustomBearerTokenAccessDeniedHandler;
import com.pixelfreebies.config.security.entrypoint.CustomBearerTokenAuthenticationEntryPoint;
import com.pixelfreebies.config.security.filter.JWTTokenGeneratorFilter;
import com.pixelfreebies.config.security.filter.JWTTokenValidatorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
public abstract class BaseSecurityConfig {

    protected final String[] permittedUrls = {
            "/",
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/forget-password/**",
            "/api/v1/file/download/**",
            "/api/v1/file/list/**",
            "/api/v1/file/search/**",
            "/api/v1/keywords/fetch/**",
            "/api/v1/file/keyword/**",

            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    };

    protected void configureCors(HttpSecurity http, List<String> permittedCorsOrigins) throws Exception {
        http.cors(corsConfig -> corsConfig.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(permittedCorsOrigins);
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setMaxAge(3600L);
            return config;
        }));
    }

    protected void configureCommonSecurity(HttpSecurity http,
                                           JWTTokenValidatorFilter jwtTokenValidatorFilter,
                                           JWTTokenGeneratorFilter jwtTokenGeneratorFilter,
                                           CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
                                           CustomBearerTokenAuthenticationEntryPoint entryPoint,
                                           CustomBearerTokenAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                .httpBasic(httpBasicConfig -> httpBasicConfig
                        .authenticationEntryPoint(customBasicAuthenticationEntryPoint)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public abstract AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder);

}
