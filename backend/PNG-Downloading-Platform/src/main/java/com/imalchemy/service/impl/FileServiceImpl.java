package com.imalchemy.service.impl;

import com.imalchemy.model.domain.File;
import com.imalchemy.model.dto.FileDTO;
import com.imalchemy.repository.FileRepository;
import com.imalchemy.service.FileService;
import com.imalchemy.service.FileStorageStrategy;
import com.imalchemy.util.converter.FileConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final FileConverter fileConverter;
    private final FileValidationService fileValidationService;
    private final FileMetadataService fileMetadataService;

    public FileServiceImpl(FileRepository fileRepository,
                           @Qualifier("parsPackDownloadHostFileStorageStrategy") FileStorageStrategy fileStorageStrategy,
                           FileConverter fileConverter,
                           FileValidationService fileValidationService,
                           FileMetadataService fileMetadataService) {
        this.fileRepository = fileRepository;
        this.fileStorageStrategy = fileStorageStrategy;
        this.fileConverter = fileConverter;
        this.fileValidationService = fileValidationService;
        this.fileMetadataService = fileMetadataService;
    }

    @Override
    public FileDTO storeFile(MultipartFile multipartFile, String fileName, String parentCategoryName,
                             List<String> subCategoryNames, List<String> dominantColors,
                             String style) throws IOException {
        try {

            this.fileValidationService.validateFileName(fileName);

            String originalFileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
            Path relativePath = this.fileStorageStrategy.store(multipartFile, originalFileName);
            File file = this.fileMetadataService.createFileDomain(multipartFile, fileName, relativePath.toString(),
                    dominantColors, style);

            this.fileMetadataService.associateFileWithCategories(file, parentCategoryName, subCategoryNames);

            return this.fileConverter.toDto(this.fileRepository.save(file));

        } catch (IOException e) {
            log.error("-> FILE -> Failed to store file: {}", e.getMessage());
            throw e;
        }
    }

    public Resource loadFileAsResource(String fileId) throws IOException {
        File file = this.fileRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new IOException("File not found with id " + fileId));
        return this.fileStorageStrategy.load(file.getFilePath());
    }

    @Override
    public List<FileDTO> listAllFiles() {
        return this.fileRepository.findAll()
                .stream().map(this.fileConverter::toDto)
                .toList();
    }

    @Override // todo: needs modification!!! not working as expected
    public List<FileDTO> searchFiles(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches
        List<File> exactMatches = this.fileRepository.searchFiles(formattedQuery);
        if (!exactMatches.isEmpty()) {
            return exactMatches.stream()
                    .filter(File::isActive)
//                    .limit(50) // Limit results
                    .map(this.fileConverter::toDto)
                    .toList();
        }

        // If no exact matches found, search for similar entries
        List<File> similarMatches = this.fileRepository.searchSimilarFiles(query);
        return similarMatches.stream()
                .filter(File::isActive)
//                .limit(50) // Limit results
                .map(this.fileConverter::toDto)
                .toList();
    }

}
