package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
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
public class LocalImageStorageStrategy extends AbstractBaseImageStorageStrategy {

    public LocalImageStorageStrategy(final FileStorageProperties fileStorageProperties) throws IOException {
        super(fileStorageProperties);
    }

    @Override
    public Path store(MultipartFile file, String originalFileName) throws IOException {
        // Resolve the path where the file will be saved
        Path targetLocation = this.fileStorageLocation.resolve(originalFileName);
        // Ensure we're not writing outside the intended directory
        if (!targetLocation.normalize().startsWith(this.fileStorageLocation.normalize())) {
            log.error("-> FILE -> Cannot store file outside the current directory: {}", originalFileName);
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

    @Override
    public Path getStorageLocation() {
        return this.fileStorageLocation;
    }

}
