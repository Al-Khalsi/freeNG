package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.PixelfreebiesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
public class ImageValidationService {

    private static final String PIXELFREEBIES_SUFFIX = " Pixelfreebies";
    private static final Pattern NUMBER_PATTERN = Pattern.compile(" \\d+$");

    public void validateImageName(String fileName) throws PixelfreebiesException {
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

    public String cleanDisplayName(String fileTitle) {
        // Remove random number if present
        String withoutNumber = NUMBER_PATTERN.matcher(fileTitle).replaceAll("");

        // Remove "pixelfreebies" suffix and capitalize
        String cleaned = withoutNumber.replace(PIXELFREEBIES_SUFFIX, "").trim();
        return this.capitalizeWords(cleaned);
    }

    public String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Split the string into words
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        // Capitalize first letter of each word
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

}
