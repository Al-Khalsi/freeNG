package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.model.dto.UpdateImageDTO;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.repository.ImageVariantRepository;
import com.pixelfreebies.service.FileService;
import com.pixelfreebies.service.ImageStorageStrategy;
import com.pixelfreebies.service.KeywordsService;
import com.pixelfreebies.util.converter.ImageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageConverter imageConverter;
    private final ImageValidationService imageValidationService;
    private final ImageMetadataService imageMetadataService;
    private final KeywordValidationService keywordValidationService;
    private final ImageCreationService imageCreationService;
    private final KeywordsService keywordsService;

    @Override
    public ImageDTO storeImage(MultipartFile uploadedMultipartFile, ImageUploadRequest imageUploadRequest) throws IOException {
        try {
            // Validate image name
            this.imageValidationService.validateImageName(imageUploadRequest.getFileName());
            String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
            Path relativePath = this.imageStorageStrategy.store(uploadedMultipartFile, originalFileName);

            // Validate keywords and retrieve their entities
            Set<Keywords> keywordsSet = this.keywordValidationService.validateAndFetchKeywords(imageUploadRequest.getKeywords());

            // Create the domains
            Image image = this.imageCreationService.createImageDomain(uploadedMultipartFile, imageUploadRequest.getFileName(), relativePath.toString(), imageUploadRequest.getDominantColors(), imageUploadRequest.getStyle(), imageUploadRequest.isLightMode());
            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(uploadedMultipartFile, relativePath.toString());

            // Associate relationships
            this.imageMetadataService.associateImageWithImageVariant(image, imageVariant);
            this.imageMetadataService.associateImageWithKeywords(image, keywordsSet);

            // Save the image (cascades save to associated entities)
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
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Page<ImageDTO> listAllImages(Pageable pageable) {
        return this.imageRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    private ImageDTO convertToDto(Image image) {
        ImageDTO imageDTO = this.imageConverter.toDto(image);
        String webpImagePath = image.getVariants()
                .stream().findFirst()
                .map(imageVariant -> {
                    if (!imageVariant.getFormat().equals(ImageFormat.WEBP)) return null;
                    return imageVariant.getFilePath();
                }).orElse(image.getFilePath());
        imageDTO.setFilePath(webpImagePath);
        imageDTO.setContentType("image/webp");
        return imageDTO;
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
    public List<String> searchKeywords(String query, int page, int size) {
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

    @Override
    public Page<ImageDTO> searchImages(String query, PageRequest pageRequest) {
        if (query == null || query.trim().isEmpty()) {
            return Page.empty();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches with pagination
        Page<Image> exactMatches = this.imageRepository.searchFiles(formattedQuery, pageRequest);

        // If exact matches are less than the requested page size, find similar matches
        if (exactMatches.getContent().isEmpty()) {
            Page<ImageDTO> map = this.imageRepository.searchSimilarFiles(query, pageRequest)
                    .map(this::convertToDto);
            return map;
        }

        return exactMatches.map(this::convertToDto);
    }

    @Override
    public Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable) {
        KeywordsDTO foundKeyword = this.keywordsService.findKeywordById(keywordId);
        return this.imageRepository.findByKeywords_Id(foundKeyword.getId(), pageable)
                .map(this.imageConverter::toDto);
    }

}
