package com.pixelfreebies.config.security;

import com.pixelfreebies.config.security.entrypoint.CustomBasicAuthenticationEntryPoint;
import com.pixelfreebies.config.security.entrypoint.CustomBearerTokenAccessDeniedHandler;
import com.pixelfreebies.config.security.entrypoint.CustomBearerTokenAuthenticationEntryPoint;
import com.pixelfreebies.config.security.filter.JWTTokenGeneratorFilter;
import com.pixelfreebies.config.security.filter.JWTTokenValidatorFilter;
import com.pixelfreebies.util.SecurityUtil;
import com.pixelfreebies.util.converter.JavaDataTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@Profile("!prod")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityUtil securityUtil;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;
    private final JavaDataTypeConverter javaDataTypeConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //TODO: configure this
                .cors(corsConfig -> corsConfig.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers( //TODO: configure to use permitted urls as dynamic and to follow DRY
                                "/",
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
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
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(this.jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(this.jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                .httpBasic(httpBasicConfig -> httpBasicConfig
                        .authenticationEntryPoint(this.customBasicAuthenticationEntryPoint)
                )
                .exceptionHandling(excHandling -> excHandling
                        .authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                        .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler)
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider(userDetailsService, passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

}