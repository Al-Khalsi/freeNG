package com.pixelfreebies.service;

import com.pixelfreebies.config.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class AbstractBaseImageStorageStrategy implements ImageStorageStrategy {

    protected final Path fileStorageLocation;

    protected AbstractBaseImageStorageStrategy(FileStorageProperties fileStorageProperties) throws IOException {
        String fileStoragePath = fileStorageProperties.getLocation();
        if (fileStoragePath == null || fileStoragePath.trim().isEmpty()) {
            log.error("-> FILE -> File location is null or empty");
            throw new IllegalStateException("File storage location must be configured");
        }

        // Set up the directory where files will be stored
        this.fileStorageLocation = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        initializeStorageLocation();
    }

    private void initializeStorageLocation() throws IOException {
        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(this.fileStorageLocation);
            // Ensure it's writable
            if (!Files.isWritable(this.fileStorageLocation)) {
                log.error("-> FILE -> File storage location is not writable: {}", this.fileStorageLocation);
                throw new IOException("File storage location is not writable: " + this.fileStorageLocation);
            }
        } catch (IOException e) {
            log.error("-> FILE -> Failed to create directory: {}. Cause=[{}]", this.fileStorageLocation, e.getMessage());
            throw new IOException("Could not create the directory where the uploaded files will be stored", e);
        }
    }

    @Override
    public Path store(MultipartFile file, String fileName) throws IOException {
        return null;
    }

    @Override
    public Resource load(String filePath) throws IOException {
        return null;
    }

    @Override
    public Path getStorageLocation() {
        return null;
    }

}
