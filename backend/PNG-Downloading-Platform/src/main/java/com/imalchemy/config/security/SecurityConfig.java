package com.imalchemy.config.security;

import com.imalchemy.config.security.entrypoint.CustomAccessDeniedHandler;
import com.imalchemy.config.security.entrypoint.CustomBasicAuthenticationEntryPoint;
import com.imalchemy.config.security.filter.JWTTokenGeneratorFilter;
import com.imalchemy.config.security.filter.JWTTokenValidatorFilter;
import com.imalchemy.util.SecurityUtil;
import com.imalchemy.util.converter.JavaDataTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
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

@Configuration
@Profile("!prod")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityUtil securityUtil;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;
    private final JavaDataTypeConverter javaDataTypeConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //TODO: configure this
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(this.javaDataTypeConverter.convertListToArray(this.securityUtil.getPERMITTED_URLS())).permitAll()
                                .requestMatchers( //TODO: configure to use permitted urls as dynamic and to follow DRY
                                        "/api/v1/auth/login",
                                        "/api/v1/auth/register",
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
                .exceptionHandling(excHandling -> excHandling
                        .authenticationEntryPoint(this.customBasicAuthenticationEntryPoint)
                        .accessDeniedHandler(this.customAccessDeniedHandler)
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