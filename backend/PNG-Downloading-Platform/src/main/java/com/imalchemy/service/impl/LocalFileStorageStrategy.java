package com.imalchemy.service.impl;

import com.imalchemy.config.FileStorageProperties;
import com.imalchemy.service.FileStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class LocalFileStorageStrategy implements FileStorageStrategy {

    private final Path fileStorageLocation;

    public LocalFileStorageStrategy(FileStorageProperties fileStorageProperties) throws IOException {
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
        // Resolve the path where the file will be saved
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        // Ensure we're not writing outside the intended directory
        if (!targetLocation.normalize().startsWith(this.fileStorageLocation.normalize())) {
            log.error("-> FILE -> Cannot store file outside the current directory: {}", fileName);
            throw new IOException("Cannot store file outside the current directory");
        }
        // Copy the file to the target location, replacing if it already exists
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        // Store only the relative path in the database
        return this.fileStorageLocation.relativize(targetLocation);
    }

    @Override
    public Resource load(String filePath) throws IOException {
        Path absolutePath = this.fileStorageLocation.resolve(filePath).normalize();
        if (!absolutePath.startsWith(this.fileStorageLocation)) {
            throw new IOException("Invalid file path");
        }
        Resource resource = new UrlResource(absolutePath.toUri());
        if (!resource.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return resource;
    }

}
