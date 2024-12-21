package com.pixelfreebies.config.debug;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class EnvironmentDebugger {

    private final Environment env;

    @PostConstruct
    public void printActiveProfiles() {
        log.info("ENV -> Active profiles: {}", Arrays.toString(this.env.getActiveProfiles()));
        log.info("ENV -> FILE_UPLOAD_LOCATION from properties: {}", this.env.getProperty("file.storage.location"));
    }

}
