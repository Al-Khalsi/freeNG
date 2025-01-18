package com.pixelfreebies.service.email.strategy;

import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.registry.EmailServiceRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GoogleSmtpEmailServiceStrategy implements EmailServiceStrategy {

    private final EmailServiceRegistry emailServiceRegistry;

    @PostConstruct
    public void register() {
        this.emailServiceRegistry.register(EmailServiceProvider.GOOGLE, this);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Implementation for sending email via Google SMTP
        System.out.println("Sending email via Google SMTP to: " + to);
    }

}
