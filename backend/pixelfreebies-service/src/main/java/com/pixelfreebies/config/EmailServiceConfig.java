package com.pixelfreebies.config;

import com.pixelfreebies.service.email.factory.EmailServiceFactory;
import com.pixelfreebies.service.email.registry.EmailServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailServiceConfig {

    @Bean
    public EmailServiceFactory emailServiceFactory(EmailServiceRegistry emailServiceRegistry) {
        return emailServiceRegistry::getService;
    }

}
