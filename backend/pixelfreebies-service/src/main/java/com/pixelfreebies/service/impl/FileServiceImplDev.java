package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.KeywordsDTO;
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
import org.springframework.context.annotation.Profile;
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

@Slf4j
@Service
@Transactional
@Profile("!prod")
@RequiredArgsConstructor
public class FileServiceImplDev implements FileService {

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
    public ImageDTO saveImage(MultipartFile uploadedMultipartFile, ImageUploadRequest imageUploadRequest) throws IOException {
        log.info("Entering storeImage method with fileName: {}", imageUploadRequest.getFileName());
        try {
            // Validate image name
            this.imageValidationService.validateImageName(imageUploadRequest.getFileName());
            String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = imageUploadRequest.getFileName() + fileExtension;
            log.info("Original file name: {} - New file name: {}", originalFileName, newFileName);
            Path relativePath = this.imageStorageStrategy.store(uploadedMultipartFile, newFileName);
            log.info("File stored at relative path: {}", relativePath);

            // Validate keywords and retrieve their entities
            Set<Keywords> keywordsSet = this.keywordValidationService.validateAndFetchKeywords(imageUploadRequest.getKeywords());
            log.info("Keywords validated and fetched: {}", keywordsSet);

            // Create the domains
            Image image = this.imageCreationService.createImageDomain(uploadedMultipartFile, relativePath.toString(), imageUploadRequest);
            log.info("Image domain created: {}", image);

            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(uploadedMultipartFile, relativePath.toString());
            log.info("Image variant created: {}", imageVariant);

            // Associate relationships
            this.imageMetadataService.associateImageWithImageVariant(image, imageVariant);
            log.info("Associated image with image variant");

            this.imageMetadataService.associateImageWithKeywords(image, keywordsSet);
            log.info("Associated image with keywords");

            // Save the image (cascades save to associated entities)
            Image savedImage = this.imageRepository.save(image);
            this.imageVariantRepository.save(imageVariant);

            log.info("Image stored successfully.");
            log.info("Image name: {}", savedImage.getFileTitle());
            log.info("Image path: {}", savedImage.getFilePath());

            return this.imageConverter.toDto(savedImage);
        } catch (IOException e) {
            log.error("-> FILE -> Failed to store file: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Resource loadImageAsResource(String fileId) throws IOException {
        log.info("Entering loadImageAsResource with fileId: {}", fileId);
        Image image = this.imageRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new IOException("File not found with id " + fileId));

        Path absolutePath = this.imageStorageStrategy.getStorageLocation().resolve(image.getFilePath()).normalize();
        log.info("Image loaded from absolute path: {}", absolutePath.toAbsolutePath());

        return this.imageStorageStrategy.load(image.getFilePath());
    }

    @Override
    public Page<ImageDTO> listAllImages(Pageable pageable) {
        log.info("Loading images with pagination: {}", pageable);
        return this.imageRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    private ImageDTO convertToDto(Image image) {
        log.info("Converting Image to DTO for image: {}", image);
        ImageDTO imageDTO = this.imageConverter.toDto(image);
        String webpImagePath = image.getVariants()
                .stream().findFirst()
                .map(imageVariant -> {
                    if (!imageVariant.getFormat().equals(ImageFormat.WEBP)) return null;
                    return imageVariant.getFilePath();
                }).orElse(image.getFilePath());
        log.info("WEBP image path: {}", webpImagePath);
        imageDTO.setFilePath(webpImagePath);
        imageDTO.setContentType("image/webp");
        return imageDTO;
    }

    @Override
    public void deleteImageById(String imageId) {
        log.info("Deleting image by id: {}", imageId);
        Image image = this.imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));
        this.imageRepository.delete(image);
        log.info("Image with id {} deleted successfully", imageId);
    }

    @Override
    public List<String> searchKeywords(String query, int page, int size) {
        log.info("Searching keywords with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            log.warn("Query is null or empty, returning empty list");
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator
        log.info("Formatted query for keywords search: {}", formattedQuery);

        // First attempt to find exact matches
        List<String> exactMatches = this.imageRepository.searchKeywords(formattedQuery);
        log.info("Exact keyword matches found: {}", exactMatches.size());

        // If no exact matches found, search for similar entries
        List<String> similarMatches = this.imageRepository.searchSimilarKeywords(query);
        log.info("Similar keyword matches found: {}", similarMatches.size());

        // Create a set of IDs to avoid duplicates
        Set<String> exactMatchIds = new HashSet<>(exactMatches);

        // Add similar matches that are not in exact matches
        List<String> combinedResults = new ArrayList<>(exactMatches);
        similarMatches.stream()
                .filter(keyword -> !exactMatchIds.contains(keyword))
                .forEach(combinedResults::add);

        log.info("Combined keyword results size: {}", combinedResults.size());
        return combinedResults.stream().toList();
    }

    @Override
    public Page<ImageDTO> searchImages(String query, PageRequest pageRequest) {
        log.info("Searching images with query: {} and pageRequest: {}", query, pageRequest);
        if (query == null || query.trim().isEmpty()) {
            log.warn("Query is null or empty, returning empty Page");
            return Page.empty();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator
        log.info("Formatted query for paginated search: {}", formattedQuery);

        // First attempt to find exact matches with pagination
        Page<Image> exactMatches = this.imageRepository.searchFiles(formattedQuery, pageRequest);
        log.info("Exact matches found: {}", exactMatches.getTotalElements());

        // If exact matches are less than the requested page size, find similar matches
        if (exactMatches.getContent().isEmpty()) {
            Page<ImageDTO> map = this.imageRepository.searchSimilarFiles(query, pageRequest)
                    .map(this::convertToDto);
            log.info("No exact matches found, returning similar matches");
            return map;
        }

        return exactMatches.map(this::convertToDto);
    }

    @Override
    public Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable) {
        log.info("Listing all images by keywordId: {} with pageable: {}", keywordId, pageable);
        KeywordsDTO foundKeyword = this.keywordsService.findKeywordById(keywordId);
        log.info("Found keyword: {}", foundKeyword);
        return this.imageRepository.findByKeywords_Id(foundKeyword.getId(), pageable)
                .map(this.imageConverter::toDto);
    }
}
