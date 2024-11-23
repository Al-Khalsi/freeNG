package com.imalchemy.service.impl;

import com.imalchemy.exception.NotFoundException;
import com.imalchemy.model.domain.Image;
import com.imalchemy.model.domain.ImageVariant;
import com.imalchemy.model.dto.ImageDTO;
import com.imalchemy.model.dto.UpdateImageDTO;
import com.imalchemy.repository.ImageRepository;
import com.imalchemy.repository.ImageVariantRepository;
import com.imalchemy.service.FileService;
import com.imalchemy.service.ImageStorageStrategy;
import com.imalchemy.util.converter.ImageConverter;
import lombok.extern.slf4j.Slf4j;
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

    private final ImageRepository imageRepository;
    private final ImageVariantRepository imageVariantRepository;
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageConverter imageConverter;
    private final ImageValidationService imageValidationService;
    private final ImageMetadataService imageMetadataService;

    public FileServiceImpl(ImageRepository imageRepository, ImageVariantRepository imageVariantRepository,
                           ImageStorageStrategy imageStorageStrategy,
                           ImageConverter imageConverter,
                           ImageValidationService imageValidationService,
                           ImageMetadataService imageMetadataService) {
        this.imageRepository = imageRepository;
        this.imageVariantRepository = imageVariantRepository;
        this.imageStorageStrategy = imageStorageStrategy;
        this.imageConverter = imageConverter;
        this.imageValidationService = imageValidationService;
        this.imageMetadataService = imageMetadataService;
    }

    @Override
    public ImageDTO storeImage(MultipartFile uploadedMultipartFile, String fileName, String parentCategoryName,
                               List<String> subCategoryNames, List<String> dominantColors,
                               String style, boolean lightMode) throws IOException {
        try {

            // Perform validations on filename and path
            this.imageValidationService.validateImageName(fileName);
            String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
            Path relativePath = this.imageStorageStrategy.store(uploadedMultipartFile, originalFileName);

            // Create the entities
            Image image = this.imageMetadataService.createImageDomain(uploadedMultipartFile, fileName, relativePath.toString(), dominantColors, style, lightMode);
            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(uploadedMultipartFile, relativePath.toString());

            // Associate relationships
            this.imageMetadataService.associateImageWithCategories(image, parentCategoryName, subCategoryNames);
            this.imageMetadataService.associateImageWithImageVariant(image, imageVariant);

            // Save to db
            Image savedImage = this.imageRepository.save(image);
            this.imageVariantRepository.save(imageVariant);

            return this.imageConverter.toDto(savedImage);

        } catch (IOException e) {
            log.error("-> FILE -> Failed to store file: {}", e.getMessage());
            throw e;
        }
    }

    public Resource loadImageAsResource(String fileId) throws IOException {
        Image image = this.imageRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new IOException("File not found with id " + fileId));
        return this.imageStorageStrategy.load(image.getFilePath());
    }

    @Override
    public List<ImageDTO> listAllImages() {
        return this.imageRepository.findAll()
                .stream().map(this.imageConverter::toDto)
                .toList();
    }

    @Override // todo: needs modification!!! not working as expected
    public List<ImageDTO> searchImages(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches
        List<Image> exactMatches = this.imageRepository.searchFiles(formattedQuery);
        if (!exactMatches.isEmpty()) {
            return exactMatches.stream()
                    .filter(Image::isActive)
//                    .limit(50) // Limit results
                    .map(this.imageConverter::toDto)
                    .toList();
        }

        // If no exact matches found, search for similar entries
        List<Image> similarMatches = this.imageRepository.searchSimilarFiles(query);
        return similarMatches.stream()
                .filter(Image::isActive)
//                .limit(50) // Limit results
                .map(this.imageConverter::toDto)
                .toList();
    }

    @Override
    public void deleteImageById(String imageId) {
        Image image = this.imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));
        this.imageRepository.delete(image);
    }

    @Override
    public ImageDTO updateImage(String imageId, UpdateImageDTO updateImageDTO) {
        Image foundImage = this.imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));

        foundImage.setFileTitle(updateImageDTO.getFileTitle());
        foundImage.setActive(updateImageDTO.isActive());
        foundImage.setKeywords(updateImageDTO.getKeywords());
        foundImage.setStyle(updateImageDTO.getStyle());
        foundImage.setLightMode(updateImageDTO.isLightMode());
        foundImage.setDominantColors(updateImageDTO.getDominantColors());
        foundImage.setAverageRating(updateImageDTO.getAverageRating());

        return this.imageConverter.toDto(this.imageRepository.save(foundImage));
    }

}
