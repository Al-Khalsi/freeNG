package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.domain.MetaInfo;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.UpdateImageDTO;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.repository.ImageVariantRepository;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.repository.MetaInfoRepository;
import com.pixelfreebies.service.FileService;
import com.pixelfreebies.service.ImageStorageStrategy;
import com.pixelfreebies.util.converter.ImageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final ImageRepository imageRepository;
    private final ImageVariantRepository imageVariantRepository;
    private final MetaInfoRepository metaInfoRepository;
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageConverter imageConverter;
    private final ImageValidationService imageValidationService;
    private final ImageMetadataService imageMetadataService;

    @Override
    public ImageDTO storeImage(MultipartFile uploadedMultipartFile, String fileName,
                               List<String> keywords, List<String> dominantColors,
                               String style, boolean lightMode) throws IOException {
        try {
            // Validate image name
            this.imageValidationService.validateImageName(fileName);
            String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
            Path relativePath = this.imageStorageStrategy.store(uploadedMultipartFile, originalFileName);

            // Validate keywords and retrieve their entities
            Set<Keywords> keywordsSet = this.imageMetadataService.validateAndFetchKeywords(keywords);

            // Create the domains
            Image image = this.imageMetadataService.createImageDomain(uploadedMultipartFile, fileName, relativePath.toString(), dominantColors, style, lightMode);
            MetaInfo imageMetaInfo = this.imageMetadataService.createImageMetaInfoDomain(image);
            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(uploadedMultipartFile, relativePath.toString());

            // Associate relationships
            this.imageMetadataService.associateImageWithImageVariant(image, imageVariant);
            this.imageMetadataService.associateImageWithKeywords(image, keywordsSet);

            // Save the image (cascades save to associated entities)
            Image savedImage = this.imageRepository.save(image);
            this.metaInfoRepository.save(imageMetaInfo);
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
                .stream().map(image -> {
                    ImageDTO imageDTO = this.imageConverter.toDto(image);
                    imageDTO.setFilePath(image.getVariants().stream()
                            .findFirst().map(ImageVariant::getFilePath)
                            .orElse(image.getFilePath())
                    );
                    return imageDTO;
                }).toList();
    }

    @Override
    public Page<ImageDTO> listAllImages(Pageable pageable) {
        return this.imageRepository.findAll(pageable)
                .map(image -> {
                    ImageDTO imageDTO = this.imageConverter.toDto(image);
                    imageDTO.setFilePath(image.getVariants().stream()
                            .findFirst().map(ImageVariant::getFilePath)
                            .orElse(image.getFilePath())
                    );
                    return imageDTO;
                });
    }

    @Override
    public List<ImageDTO> searchImages(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches
        List<Image> exactMatches = this.imageRepository.searchFiles(formattedQuery);
        // If no exact matches found, search for similar entries
        List<Image> similarMatches = this.imageRepository.searchSimilarFiles(query);

        // Create a set of IDs to avoid duplicates
        Set<UUID> exactMatchIds = exactMatches.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        // Add similar matches that are not in exact matches
        List<Image> combinedResults = new ArrayList<>(exactMatches);
        similarMatches.stream()
                .filter(image -> !exactMatchIds.contains(image.getId()))
                .forEach(combinedResults::add);

        return combinedResults.stream()
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
        foundImage.setStyle(updateImageDTO.getStyle());
        foundImage.setLightMode(updateImageDTO.isLightMode());
        foundImage.setDominantColors(updateImageDTO.getDominantColors());
        foundImage.setAverageRating(updateImageDTO.getAverageRating());

        return this.imageConverter.toDto(this.imageRepository.save(foundImage));
    }

    @Override
    public List<String> searchKeywords(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches
        List<String> exactMatches = this.imageRepository.searchKeywords(formattedQuery);
        // If no exact matches found, search for similar entries
        List<String> similarMatches = this.imageRepository.searchSimilarKeywords(query);

        // Create a set of IDs to avoid duplicates
        Set<String> exactMatchIds = new HashSet<>(exactMatches);

        // Add similar matches that are not in exact matches
        List<String> combinedResults = new ArrayList<>(exactMatches);
        similarMatches.stream()
                .filter(keyword -> !exactMatchIds.contains(keyword))
                .forEach(combinedResults::add);

        return combinedResults.stream()
//                .limit(50) // Limit results
                .toList();
    }

}
