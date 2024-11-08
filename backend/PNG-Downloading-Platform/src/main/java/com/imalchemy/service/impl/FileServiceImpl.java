package com.imalchemy.service.impl;

import com.imalchemy.config.FileStorageProperties;
import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.File;
import com.imalchemy.model.dto.FileDTO;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.repository.FileRepository;
import com.imalchemy.service.FileService;
import com.imalchemy.util.SecurityUtil;
import com.imalchemy.util.converter.FileConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final CategoryRepository categoryRepository;
    private final Path fileStorageLocation;
    private final SecurityUtil securityUtil;
    private final FileConverter fileConverter;

    public FileServiceImpl(FileStorageProperties fileStorageProperties, FileRepository fileRepository, CategoryRepository categoryRepository, SecurityUtil securityUtil, FileConverter fileConverter) throws IOException {
        this.fileRepository = fileRepository;
        this.categoryRepository = categoryRepository;
        this.securityUtil = securityUtil;
        this.fileConverter = fileConverter;
        String fileStoragePath = fileStorageProperties.getLocation();

        if (fileStoragePath == null || fileStoragePath.trim().isEmpty()) {
            log.error("-> FILE -> File location is null or empty");
            throw new IllegalStateException("File storage location must be configured");
        }

        // Set up the directory where files will be stored
        this.fileStorageLocation = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        log.info("-> FILE -> File storage location set. File Location: {}", fileStoragePath);

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
    public FileDTO storeFile(MultipartFile multipartFile, String parentCategoryName, List<String> subCategoryNames, List<String> dominantColors, String style) throws IOException {
        try {
            // Clean the filename to remove any potential security risks
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            // Prevent directory traversal attacks
            if (fileName.contains("..")) {
                log.error("-> FILE -> File name contains invalid path sequence: {}", fileName);
                throw new IOException("Filename contains invalid path sequence " + fileName);
            }
            // Resolve the path where the file will be saved
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            // Ensure we're not writing outside the intended directory
            if (!targetLocation.normalize().startsWith(this.fileStorageLocation.normalize())) {
                log.error("-> FILE -> Cannot store file outside the current directory: {}", fileName);
                throw new IOException("Cannot store file outside the current directory");
            }
            // Copy the file to the target location, replacing if it already exists
            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Store only the relative path in the database
            String relativePath = this.fileStorageLocation.relativize(targetLocation).toString();

            // Create a new File to store in the database
            File file = createFileDomain(multipartFile, fileName, relativePath, parentCategoryName, subCategoryNames, dominantColors, style);

            // Save the file metadata to the database and return the entity
            return this.fileConverter.toDto(this.fileRepository.save(file));
        } catch (IOException e) {
            log.error("-> FILE -> Failed to store file: {}", e.getMessage());
            throw e;
        }
    }

    private File createFileDomain(MultipartFile multipartFile, String fileName, String relativePath, String parentCategoryName, List<String> subCategoryNames, List<String> dominantColors, String style) throws IOException {
        File file = new File();
        file.setFileTitle(fileName);
        file.setFilePath(relativePath); // Store only the relative path
        file.setContentType(multipartFile.getContentType());
        file.setSize(multipartFile.getSize());
        file.setActive(true);
        file.setAverageRating(BigDecimal.ZERO);
        file.setDownloadCount(0);
        file.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        file.setHeight(0);
        file.setWidth(0);
        file.setStyle(style);
        dominantColors.forEach(dominantColor -> file.getDominantColors().add(dominantColor));

        subCategoryNames.forEach(subCategoryName -> {
            Category parentCategory = this.categoryRepository.findByNameIgnoreCase(parentCategoryName)
                    .orElse(this.categoryRepository.findByNameIgnoreCase("defaultCategory").get());
            file.getCategories().add(parentCategory);
            if (!subCategoryName.isEmpty()) {
                Category subCategory = this.categoryRepository.findByNameIgnoreCase(subCategoryName)
                        .orElse(null);
                file.getCategories().add(subCategory);
            }
        });

        return file;
    }

    // Method to load a file as a Resource
    public Resource loadFileAsResource(String fileId) throws IOException {
        // Find the file metadata in the database
        File file = this.fileRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new IOException("File not found with id " + fileId));

        try {
            // Combine the base storage location with the relative path from the database
            Path absolutePath = this.fileStorageLocation.resolve(file.getFilePath()).normalize();

            // Ensure the resolved path is still within our base directory (security check)
            if (!absolutePath.startsWith(this.fileStorageLocation)) {
                log.error("-> FILE -> File cannot be stored outside the storage location directory: {}", file.getFilePath());
                throw new IOException("Invalid file path");
            }

            Resource resource = new UrlResource(absolutePath.toUri());
            if (resource.exists()) return resource;
            else throw new IOException("File not found: " + file.getFileTitle());

        } catch (MalformedURLException ex) {
            log.error("File not found. Filename: {} - Cause: [{}]", file.getFileTitle(), ex.getMessage());
            throw new IOException("File not found " + file.getFileTitle(), ex);
        }
    }

}
