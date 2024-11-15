package com.imalchemy.service.impl;

import com.imalchemy.model.domain.File;
import com.imalchemy.model.dto.FileDTO;
import com.imalchemy.repository.FileRepository;
import com.imalchemy.service.FileService;
import com.imalchemy.service.FileStorageStrategy;
import com.imalchemy.util.converter.FileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileStorageStrategy fileStorageStrategy;
    private final FileConverter fileConverter;
    private final FileValidationService fileValidationService;
    private final FileMetadataService fileMetadataService;

    @Override
    public FileDTO storeFile(MultipartFile multipartFile, String fileName, String parentCategoryName,
                             List<String> subCategoryNames, List<String> dominantColors,
                             String style) throws IOException {
        try {

            this.fileValidationService.validateFile(fileName);

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

}
