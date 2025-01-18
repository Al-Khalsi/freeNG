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
            this.sendHtmlEmail();
        } catch (MessagingException e) {
            log.error("ERROR sending email: {}", e.getMessage(), e);
            throw new PixelfreebiesException(e.getMessage());
        }
    }

    private void sendHtmlEmail() throws MessagingException {
        MimeMessage message = this.javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(MimeMessage.RecipientType.TO, "duke.of.java.spring@gmail.com");
        message.setSubject("Test email from Spring");

        String htmlContent = this.prepareHTMLContent();
        message.setContent(htmlContent, "text/html; charset=utf-8");

        this.javaMailSender.send(message);
    }

    private String prepareHTMLContent() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Reset Your Password</title>
                        <style>
                            body {
                                margin: 0;
                                padding: 0;
                                font-family: 'Arial', sans-serif;
                                background-color: #12102b;
                                color: #ffffff;
                            }
                            .email-container {
                                max-width: 600px;
                                margin: 20px auto;
                                background-color: #1c1b3a;
                                border-radius: 10px;
                                overflow: hidden;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                            }
                            .header {
                                padding: 20px;
                                text-align: center;
                                background-color: #362465;
                                color: #ffffff;
                            }
                            .header h1 {
                                margin: 0;
                                font-size: 24px;
                            }
                            .header h1 span {
                                font-size: 36px;
                                color: #6f42c1;
                            }
                            .content {
                                padding: 20px;
                                line-height: 1.6;
                            }
                            .otp {
                                display: block;
                                margin: 20px 0;
                                text-align: center;
                                font-size: 24px;
                                color: #6f42c1;
                                font-weight: bold;
                            }
                            .footer {
                                padding: 10px;
                                text-align: center;
                                background-color: #362465;
                                color: #bbbbbb;
                                font-size: 14px;
                            }
                            .footer a {
                                color: #6f42c1;
                                text-decoration: none;
                            }
                            .footer a:hover {
                                text-decoration: underline;
                            }
                            .button {
                                display: inline-block;
                                padding: 10px 20px;
                                background-color: #6f42c1;
                                color: #ffffff;
                                text-decoration: none;
                                border-radius: 5px;
                                margin: 10px auto;
                                text-align: center;
                                font-size: 16px;
                            }
                            .button:hover {
                                background-color: #54328c;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="email-container">
                            <div class="header">
                                <h1><span>P</span>ixelFreebies</h1>
                                <p>Your first go-to image website</p>
                            </div>
                            <div class="content">
                                <p>Hi [User],</p>
                                <p>You requested to reset your password. Use the one-time OTP below to reset it. Please note that this OTP is valid only for the next 10 minutes.</p>
                                <div class="otp">123456</div>
                                <p>If you did not request this password reset, please ignore this email or <a href="#">contact support</a> if you have concerns.</p>
                                <p>Click the button below to reset your password:</p>
                                <a href="https://pixelfreebies.com/reset-password" class="button">Reset Password</a>
                                <p>Thank you,<br>PixelFreebies Team</p>
                            </div>
                            <div class="footer">
                                <p>&copy; 2025 PixelFreebies. All Rights Reserved.</p>
                                <p><a href="https://pixelfreebies.com/privacy">Privacy Policy</a> | <a href="https://pixelfreebies.com/terms">Terms of Service</a></p>
                            </div>
                        </div>
                    </body>
                </html>
                """;
    }

}
