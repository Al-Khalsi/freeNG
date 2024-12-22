package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.properties.FileStorageProperties;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.service.ImageStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@Primary
@Profile("!prod")
public class LocalImageStorageStrategy implements ImageStorageStrategy {

    protected final Path fileStorageLocation;

    public LocalImageStorageStrategy(FileStorageProperties fileStorageProperties) throws PixelfreebiesException {
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
    public Path store(MultipartFile file, String originalFileName) throws PixelfreebiesException, IOException {
        log.info("Storing file: {} with original file name: {}", file.getOriginalFilename(), originalFileName);
        // Resolve the path where the file will be saved
        Path targetLocation = this.fileStorageLocation.resolve(originalFileName);
        log.info("Resolved target location: {}", targetLocation);

        // Ensure we're not writing outside the intended directory
        if (!targetLocation.normalize().startsWith(this.fileStorageLocation.normalize())) {
            log.error("-> FILE -> Cannot store file outside the current directory: {}", originalFileName);
            throw new PixelfreebiesException("Cannot store file outside the current directory", INTERNAL_SERVER_ERROR);
        }
        // Copy the file to the target location, replacing if it already exists
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image stored at: {}", targetLocation.toAbsolutePath());
        // Store only the relative path in the database
        return this.fileStorageLocation.relativize(targetLocation);
    }

    @Override
    public Path getStorageLocation() {
        log.info("Getting storage location: {}", this.fileStorageLocation);
        return this.fileStorageLocation;
    }

}
