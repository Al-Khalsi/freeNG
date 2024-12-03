package com.pixelfreebies.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Slf4j
@Configuration
@Profile("prod")
public class FTPConfig {

    private @Value("${ftp.host}") String host;
    private @Value("${ftp.username}") String username;
    private @Value("${ftp.password}") String password;

    @Bean
    public FTPClient ftpClient() {
        FTPClient ftpClient = new FTPClient();
        log.info("Connecting to FTP server...");
        try {

            ftpClient.connect(this.host);
            ftpClient.login(this.username, this.password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            log.info("Successfully connected to FTP server.");

            return ftpClient;

        } catch (IOException e) {
            log.error("Failed to connect to FTP server: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to FTP server", e);
        }
    }

}
