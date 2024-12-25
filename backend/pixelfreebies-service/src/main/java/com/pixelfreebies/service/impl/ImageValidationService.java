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

    public String replaceSpacesWithHyphens(String input) {
        if (input == null) throw new NullPointerException("Input is null");
        return input.trim().replace(" ", "-");
    }

    public String replaceHyphensWithSpaces(String input) {
        if (input == null) throw new NullPointerException("Input is null");
        return input.trim().replace("-", " ");
    }

    public String removeLastTwoWords(String input) {
        String[] words = input.split(" ");

        StringBuilder result = new StringBuilder();

        // Append all words except the last two
        for (int i = 0; i < words.length - 2; i++) {
            result.append(words[i]);
            result.append(" "); // Add a space between words
        }

        return result.toString().trim();
    }

}
