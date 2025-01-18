package com.pixelfreebies.service.email.strategy;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.service.email.registry.EmailServiceRegistry;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSmtpEmailServiceStrategyTest {

    private @Mock JavaMailSender javaMailSender;
    private @Mock EmailServiceRegistry emailServiceRegistry;
    private @Mock MimeMessage mimeMessage;

    private @InjectMocks GoogleSmtpEmailServiceStrategy googleSmtpEmailServiceStrategy;


    @BeforeEach
    public void setUp() {
        // Mock the behavior of JavaMailSender to return a mock MimeMessage
        when(this.javaMailSender.createMimeMessage())
                .thenReturn(this.mimeMessage);
    }

    @Test
    public void testSendEmail() throws MessagingException, PixelfreebiesException {
        // Given
        String to = "duke.of.java.spring@gmail.com";
        String subject = "Test email from Spring";
        String body = """
                <h1>This is a test Spring Boot email</h1>
                <p>It can contain <strong>HTML</strong> content.</p>
                """;

        // When
        this.googleSmtpEmailServiceStrategy.sendEmail(to, subject, body);

        // Then
        verify(this.mimeMessage).setFrom(new InternetAddress("pixelfreebies@gmail.com"));
        verify(this.mimeMessage).setRecipients(MimeMessage.RecipientType.TO, to);
        verify(this.mimeMessage).setSubject(subject);
        verify(this.mimeMessage).setContent(anyString(), eq("text/html; charset=utf-8"));

        // Verify
        verify(this.javaMailSender).send(this.mimeMessage);
    }

}