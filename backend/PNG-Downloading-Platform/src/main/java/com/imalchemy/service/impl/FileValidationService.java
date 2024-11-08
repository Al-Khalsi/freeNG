package com.imalchemy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Service
public class FileValidationService {

    public void validateFile(String fileName) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            log.error("-> FILE -> File name is null or empty");
            throw new IOException("File name cannot be empty");
        }

        // Clean the filename to remove any potential security risks
        String cleanFileName = StringUtils.cleanPath(fileName);
        // Prevent directory traversal attacks
        if (cleanFileName.contains("..")) {
            log.error("-> FILE -> File name contains invalid path sequence: {}", fileName);
            throw new IOException("Filename contains invalid path sequence: " + cleanFileName);
        }
    }

}
