package com.imalchemy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication
public class PngDownloadingPlatformApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PngDownloadingPlatformApplication.class, args);

        ConfigurableEnvironment environment = context.getEnvironment();
        log.info("Database url: {}", environment.getProperty("spring.datasource.url"));
        log.info("Database user: {}", environment.getProperty("spring.datasource.username"));
        log.info("Database password: {}", environment.getProperty("spring.datasource.password"));
    }

}
