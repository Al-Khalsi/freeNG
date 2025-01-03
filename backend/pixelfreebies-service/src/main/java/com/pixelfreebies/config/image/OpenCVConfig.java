package com.pixelfreebies.config.image;

import nu.pattern.OpenCV;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCVConfig {

    @Bean
    public String loadOpenCVNativeLibraries() {
        OpenCV.loadLocally();
        return "loaded";
    }

}
