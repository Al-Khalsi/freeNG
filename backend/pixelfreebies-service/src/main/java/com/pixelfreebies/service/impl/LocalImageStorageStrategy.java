package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.properties.FileStorageProperties;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.service.AbstractBaseImageStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@Primary
@Profile("!prod")
public class LocalImageStorageStrategy extends AbstractBaseImageStorageStrategy {

    public LocalImageStorageStrategy(final FileStorageProperties fileStorageProperties) throws PixelfreebiesException {
        super(fileStorageProperties);
        log.info("LocalImageStorageStrategyDev initialized with storage location: {}", fileStorageProperties.getLocation());
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
