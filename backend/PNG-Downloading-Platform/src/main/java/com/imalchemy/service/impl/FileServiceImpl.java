package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Image;
import com.imalchemy.model.dto.ImageDTO;
import com.imalchemy.model.dto.ImageDTO;
import com.imalchemy.repository.FileRepository;
import com.imalchemy.service.FileService;
import com.imalchemy.service.FileStorageStrategy;
import com.imalchemy.util.converter.ImageConverter;
import lombok.extern.slf4j.Slf4j;
import com.imalchemy.util.converter.ImageConverter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileStorageStrategy fileStorageStrategy;
    private final ImageConverter imageConverter;
    private final FileValidationService fileValidationService;
    private final FileMetadataService fileMetadataService;

    public FileServiceImpl(FileRepository fileRepository,
                           FileStorageStrategy fileStorageStrategy,
                           ImageConverter imageConverter,
                           FileValidationService fileValidationService,
                           FileMetadataService fileMetadataService) {
        this.fileRepository = fileRepository;
        this.fileStorageStrategy = fileStorageStrategy;
        this.imageConverter = imageConverter;
        this.fileValidationService = fileValidationService;
        this.fileMetadataService = fileMetadataService;
    }

    @Override
    public ImageDTO storeFile(MultipartFile multipartFile, String fileName, String parentCategoryName,
                              List<String> subCategoryNames, List<String> dominantColors,
                              String style) throws IOException {
        try {

            this.fileValidationService.validateFileName(fileName);

            String originalFileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
            Path relativePath = this.fileStorageStrategy.store(multipartFile, originalFileName);
            Image image = this.fileMetadataService.createImageDomain(multipartFile, fileName, relativePath.toString(),
                    dominantColors, style);

            this.fileMetadataService.associateImageWithCategories(image, parentCategoryName, subCategoryNames);

            return this.imageConverter.toDto(this.fileRepository.save(image));

        } catch (IOException e) {
            log.error("-> FILE -> Failed to store file: {}", e.getMessage());
            throw e;
        }
    }

    public Resource loadFileAsResource(String fileId) throws IOException {
        Image image = this.fileRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new IOException("File not found with id " + fileId));
        return this.fileStorageStrategy.load(image.getFilePath());
    }

    @Override
    public List<ImageDTO> listAllFiles() {
        return this.fileRepository.findAll()
                .stream().map(this.imageConverter::toDto)
                .toList();
    }

    @Override // todo: needs modification!!! not working as expected
    public List<ImageDTO> searchFiles(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches
        List<Image> exactMatches = this.fileRepository.searchFiles(formattedQuery);
        if (!exactMatches.isEmpty()) {
            return exactMatches.stream()
                    .filter(Image::isActive)
//                    .limit(50) // Limit results
                    .map(this.imageConverter::toDto)
                    .toList();
        }

        // If no exact matches found, search for similar entries
        List<Image> similarMatches = this.fileRepository.searchSimilarFiles(query);
        return similarMatches.stream()
                .filter(Image::isActive)
//                .limit(50) // Limit results
                .map(this.imageConverter::toDto)
                .toList();
    }

}
