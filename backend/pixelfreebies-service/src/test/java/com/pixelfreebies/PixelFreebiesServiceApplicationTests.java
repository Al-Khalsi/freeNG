package com.pixelfreebies;

import com.pixelfreebies.service.email.strategy.GoogleSmtpEmailServiceStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PixelFreebiesServiceApplicationTests {

    private @Autowired GoogleSmtpEmailServiceStrategy emailService;

    @Test
    void contextLoads() {
    }

    @Test
    void test_SendEmail() {
        System.setProperty("SPRING_MAIL_PASSWORD", "_");
        this.emailService.sendEmail("", "", "");
    }

}
