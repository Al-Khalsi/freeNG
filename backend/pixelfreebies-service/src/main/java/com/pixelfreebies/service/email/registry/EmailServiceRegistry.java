package com.pixelfreebies.service.email.registry;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.strategy.EmailServiceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
public class EmailServiceRegistry {

    private final Map<EmailServiceProvider, EmailServiceStrategy> emailServices = new EnumMap<>(EmailServiceProvider.class);

    public void register(EmailServiceProvider provider, EmailServiceStrategy emailServiceStrategy) {
        this.emailServices.put(provider, emailServiceStrategy);
        log.debug("Registered email service provider: {}, service implementation: {}", provider, emailServiceStrategy.getClass().getSimpleName());
    }

    public EmailServiceStrategy getServiceStrategy(EmailServiceProvider provider) throws PixelfreebiesException {
        EmailServiceStrategy emailServiceStrategy = this.emailServices.get(provider);
        if (emailServiceStrategy == null) {
            throw new PixelfreebiesException("No email service found for provider: " + provider);
        }
        log.debug("Retrieved email service provider: {}, service implementation: {}", provider, emailServiceStrategy.getClass().getSimpleName());
        return emailServiceStrategy;
    }

}
