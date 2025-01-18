package com.pixelfreebies.service.email.strategy;

import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.registry.EmailServiceRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MailtrapEmailServiceStrategy implements EmailServiceStrategy {

    private final EmailServiceRegistry emailServiceRegistry;

    @PostConstruct
    public void register() {
        this.emailServiceRegistry.register(EmailServiceProvider.MAIL_TRAP, this);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
