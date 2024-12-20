package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.properties.FileStorageProperties;
import com.pixelfreebies.service.AbstractBaseImageStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
@Primary
@Profile("!prod")
public class LocalImageStorageStrategy extends AbstractBaseImageStorageStrategy {

    public LocalImageStorageStrategy(final FileStorageProperties fileStorageProperties) throws IOException {
        super(fileStorageProperties);
        log.info("LocalImageStorageStrategyDev initialized with storage location: {}", fileStorageProperties.getLocation());
    }

    @Override
    public Path store(MultipartFile file, String originalFileName) throws IOException {
        log.info("Storing file: {} with original file name: {}", file.getOriginalFilename(), originalFileName);
        // Resolve the path where the file will be saved
        Path targetLocation = this.fileStorageLocation.resolve(originalFileName);
        log.info("Resolved target location: {}", targetLocation);

        // Ensure we're not writing outside the intended directory
        if (!targetLocation.normalize().startsWith(this.fileStorageLocation.normalize())) {
            log.error("-> FILE -> Cannot store file outside the current directory: {}", originalFileName);
            throw new IOException("Cannot store file outside the current directory");
        }
        // Copy the file to the target location, replacing if it already exists
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image stored at: {}", targetLocation.toAbsolutePath());
        // Store only the relative path in the database
        return this.fileStorageLocation.relativize(targetLocation);
    }

    @Override
    public Resource load(String filePath) throws IOException {
        log.info("Loading file with path: {}", filePath);
        Path absolutePath = this.fileStorageLocation.resolve(filePath).normalize();
        log.info("Resolved absolute path: {}", absolutePath);

        if (!absolutePath.startsWith(this.fileStorageLocation)) {
            log.error("Invalid file path: {}", filePath);
            throw new IOException("Invalid file path");
        }
        Resource resource = new UrlResource(absolutePath.toUri());
        if (!resource.exists()) {
            log.error("File not found: {}", filePath);
            throw new IOException("File not found: " + filePath);
        }
        log.info("Image loaded - imagePath: {}", filePath);
        return resource;
    }

    @Override
    public Path getStorageLocation() {
        log.info("Getting storage location: {}", this.fileStorageLocation);
        return this.fileStorageLocation;
    }

}
