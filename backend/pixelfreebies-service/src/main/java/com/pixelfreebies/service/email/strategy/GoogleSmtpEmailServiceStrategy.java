package com.pixelfreebies.service.email.strategy;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.registry.EmailServiceRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSmtpEmailServiceStrategy implements EmailServiceStrategy {

    private final EmailServiceRegistry emailServiceRegistry;
    private final JavaMailSender javaMailSender;
    private @Value("${spring.mail.username}") String FROM = "pixelfreebies@gmail.com";

    @PostConstruct
    public void register() {
        this.emailServiceRegistry.register(EmailServiceProvider.GOOGLE, this);
    }

    @Override
    public void sendEmail(String to, String subject, String body) throws PixelfreebiesException {
        try {
            this.sendHtmlEmail(to, subject, body);
        } catch (MessagingException e) {
            log.error("ERROR sending email: {}", e.getMessage(), e);
            throw new PixelfreebiesException(e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = this.javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");

        this.javaMailSender.send(message);
    }

}
