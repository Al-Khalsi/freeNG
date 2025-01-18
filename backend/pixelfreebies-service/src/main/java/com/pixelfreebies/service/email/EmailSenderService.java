package com.pixelfreebies.service.email;

import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.factory.EmailServiceFactory;
import com.pixelfreebies.service.email.strategy.EmailServiceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final EmailServiceFactory emailServiceFactory;

    private @Value("${email.service.provider}") String emailServiceProvider;

    public void sendEmail(String to, String subject, String body) {
        // Create the implementation class from the configured email provider
        EmailServiceProvider provider = EmailServiceProvider.fromName(this.emailServiceProvider);
        EmailServiceStrategy emailServiceStrategy = this.emailServiceFactory.createEmailService(provider);

        // Send the email
        emailServiceStrategy.sendEmail(to, subject, body);
    }

}
