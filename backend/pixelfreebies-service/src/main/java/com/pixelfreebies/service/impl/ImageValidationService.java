package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.PixelfreebiesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
public class ImageValidationService {

    public void validateImageName(String fileName) throws IOException, PixelfreebiesException {
        if (fileName == null || fileName.trim().isEmpty()) {
            log.error("-> FILE -> File name is null or empty");
            throw new PixelfreebiesException("File name cannot be empty", INTERNAL_SERVER_ERROR);
        }

        // Clean the filename to remove any potential security risks
        String cleanFileName = StringUtils.cleanPath(fileName);
        // Prevent directory traversal attacks
        if (cleanFileName.contains("..")) {
            log.error("-> FILE -> File name contains invalid path sequence: {}", fileName);
            throw new PixelfreebiesException("Filename contains invalid path sequence: " + cleanFileName, INTERNAL_SERVER_ERROR);
        }
    }

}
