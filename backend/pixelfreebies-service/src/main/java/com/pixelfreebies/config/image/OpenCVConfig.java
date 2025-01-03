package com.pixelfreebies.config.image;

import nu.pattern.OpenCV;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCVConfig {

    @Bean
    public String loadOpenCVNativeLibraries() {
        OpenCV.loadLocally(); // Loads OpenCV native libraries from the local path or bundled resources
        return "opencv native libraries loaded";
    }

}
