package com.pixelfreebies.service;

import com.pixelfreebies.config.properties.FileStorageProperties;
import com.pixelfreebies.exception.PixelfreebiesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class AbstractBaseImageStorageStrategy implements ImageStorageStrategy {

    protected final Path fileStorageLocation;

    protected AbstractBaseImageStorageStrategy(FileStorageProperties fileStorageProperties) throws PixelfreebiesException {
        String fileStoragePath = fileStorageProperties.getLocation();
        if (fileStoragePath == null || fileStoragePath.trim().isEmpty()) {
            log.error("-> FILE -> File location is null or empty");
            throw new PixelfreebiesException("File storage location must be configured", INTERNAL_SERVER_ERROR);
        }

        // Set up the directory where files will be stored
        this.fileStorageLocation = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        initializeStorageLocation();
    }

    private void initializeStorageLocation() throws PixelfreebiesException {
        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(this.fileStorageLocation);
            // Ensure it's writable
            if (!Files.isWritable(this.fileStorageLocation)) {
                log.error("-> FILE -> File storage location is not writable: {}", this.fileStorageLocation);
                throw new PixelfreebiesException("File storage location is not writable: " + this.fileStorageLocation, INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            log.error("-> FILE -> Failed to create directory: {}. Cause=[{}]", this.fileStorageLocation, e.getMessage());
            throw new PixelfreebiesException("Could not create the directory where the uploaded files will be stored: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Path store(MultipartFile file, String fileName) throws PixelfreebiesException, IOException {
        return null;
    }

    @Override
    public void storeWebp(BufferedImage image, String remotePath, float quality, boolean lossless) throws PixelfreebiesException {

    }

    @Override
    public Path getStorageLocation() {
        return null;
    }

}
