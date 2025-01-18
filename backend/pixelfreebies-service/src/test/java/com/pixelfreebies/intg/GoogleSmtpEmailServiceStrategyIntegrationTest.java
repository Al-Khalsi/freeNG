package com.pixelfreebies.intg;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.registry.EmailServiceRegistry;
import com.pixelfreebies.service.email.strategy.EmailServiceStrategy;
import com.pixelfreebies.service.email.strategy.GoogleSmtpEmailServiceStrategy;
import jakarta.mail.internet.MimeMessage;
import org.eclipse.angus.mail.util.MailConnectException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class GoogleSmtpEmailServiceStrategyIntegrationTest {

    private @Autowired GoogleSmtpEmailServiceStrategy emailService;
    private @Autowired EmailServiceRegistry emailServiceRegistry;
    private GreenMail greenMail;

    // These credentials must match exactly between GreenMail and JavaMailSender
    private static final String TEST_EMAIL = "pixelfreebies@gmail.com";
    private static final String TEST_PASSWORD = "testpass";

    @BeforeEach
    void setUp() {
        // Configure GreenMail to use specific credentials
        ServerSetup setup = new ServerSetup(2525, "localhost", ServerSetup.PROTOCOL_SMTP);
        this.greenMail = new GreenMail(setup);

        // Create a user in GreenMail
        this.greenMail.setUser(TEST_EMAIL, TEST_PASSWORD);
        this.greenMail.start();
    }

    @AfterEach
    void tearDown() {
        this.greenMail.stop();
    }

    @TestConfiguration
    static class TestConfig {

        @Primary  // override the existing JavaMailSender autoconfiguration
        @Bean
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            mailSender.setPort(2525);
            mailSender.setUsername(TEST_EMAIL);
            mailSender.setPassword(TEST_PASSWORD);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.debug", "true");
            props.put("mail.smtp.ssl.trust", "localhost");
            props.put("mail.smtp.ssl.checkserveridentity", "false");

            return mailSender;
        }

    }

    @Test
    void shouldSendEmail() throws Exception {
        // Given
        String to = "duke.of.java.spring@gmail.com";
        String subject = "Test email from Spring";
        String body = "<h1>This is a test Spring Boot email</h1>";

        // When
        this.emailService.sendEmail(to, subject, body);

        // Then
        MimeMessage[] receivedMessages = this.greenMail.getReceivedMessages();
        assertThat(receivedMessages)
                .hasSize(1);

        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getSubject())
                .isEqualTo(subject);
        assertThat(receivedMessage.getAllRecipients()[0].toString())
                .isEqualTo(to);

        // Check content
        String content = GreenMailUtil.getBody(receivedMessage);
        assertThat(content)
                .contains("This is a test Spring Boot email");
    }

    @Test
    void shouldBeRegisteredWithCorrectProvider() {
        // Given & When
        EmailServiceStrategy strategy = this.emailServiceRegistry.getServiceStrategy(EmailServiceProvider.GOOGLE);

        // Then
        assertThat(strategy)
                .isNotNull()
                .isInstanceOf(GoogleSmtpEmailServiceStrategy.class);
    }

    @Test
    void shouldThrowExceptionWhenSendingFails() {
        // Given
        this.greenMail.stop(); // Stop mail server to simulate failure
        String to = "duke.of.java.spring@gmail.com";
        String subject = "Test email";
        String body = "Test body";

        // When & Then
        assertThatThrownBy(() -> this.emailService.sendEmail(to, subject, body))
                .isInstanceOf(MailSendException.class);
    }

}
