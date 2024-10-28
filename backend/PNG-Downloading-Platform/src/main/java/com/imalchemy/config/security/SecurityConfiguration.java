package com.imalchemy.config.security;

import com.imalchemy.config.security.entrypoint.CustomAccessDeniedHandler;
import com.imalchemy.config.security.entrypoint.CustomBasicAuthenticationEntryPoint;
import com.imalchemy.config.security.filter.JWTTokenGeneratorFilter;
import com.imalchemy.config.security.filter.JWTTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@Profile("!prod")
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private @Value("${base.url}") String BASE_URL;

    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        http
                .csrf(csrfConfig -> csrfConfig
//                                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//                                .ignoringRequestMatchers(this.BASE_URL + "/register", this.BASE_URL + "/login")
                                .disable()
                )
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(this.BASE_URL + "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(this.jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(this.jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(this.customBasicAuthenticationEntryPoint)
                )
                .exceptionHandling(excHandling -> excHandling
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