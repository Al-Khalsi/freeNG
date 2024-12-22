package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.properties.S3Properties;
import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.exception.PixelfreebiesException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
    private final S3Properties s3Properties;
    private final MinioS3Service minioS3Service;

    @Override
    public ImageDTO saveImage(MultipartFile uploadedMultipartFile, ImageUploadRequest imageUploadRequest) {
        try {
            // Validate image name
            this.imageValidationService.validateImageName(imageUploadRequest.getFileName());
            String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = imageUploadRequest.getFileName() + fileExtension;
            Path relativePath = this.imageStorageStrategy.store(uploadedMultipartFile, newFileName);

            // Validate keywords and retrieve their entities
            Set<Keywords> keywordsSet = this.keywordValidationService.validateAndFetchKeywords(imageUploadRequest.getKeywords());

            // Create the domains
            Image image = this.imageCreationService.createImageDomain(uploadedMultipartFile, relativePath.toString(), imageUploadRequest);
            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(uploadedMultipartFile, relativePath.toString());

            // Set full paths
            String normalizedPath = relativePath.toString().replace("\\", "/");
            String imagePath = this.getFullPath(normalizedPath);
            image.setFilePath(imagePath);

            String normalizedWebpFilePath = imageVariant.getFilePath().replace("\\", "/");
            String webpPath = this.getFullPath(normalizedWebpFilePath);
            imageVariant.setFilePath(webpPath);

            // Associate relationships
            this.imageMetadataService.associateImageWithImageVariant(image, imageVariant);
            this.imageMetadataService.associateImageWithKeywords(image, keywordsSet);

            // Save the image (cascades save to associated entities)
            Image savedImage = this.imageRepository.save(image);
            this.imageVariantRepository.save(imageVariant);

            return this.imageConverter.toDto(savedImage);
        } catch (IOException e) {
            log.error("-> FILE -> Failed to save the image: {}", e.getMessage());
            throw new PixelfreebiesException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public String getFullPath(String objectName) {
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return String.format("https://%s/%s/%s", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket(), objectName);
    }

    @Override
    public Page<ImageDTO> listAllImages(Pageable pageable) {
        return this.imageRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public ImageDTO findImageById(UUID fileId) {
        return this.imageRepository.findById(fileId)
                .map(this.imageConverter::toDto)
                .orElseThrow(() -> new NotFoundException("File not found with id " + fileId));
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
    public void deleteImageById(String imageId) throws PixelfreebiesException {
        Image image = this.imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));
        image.getVariants().stream()
                .map(imageVariant -> {
                    Optional<ImageVariant> optionalImageVariant = this.imageVariantRepository.findById(imageVariant.getId());
                    if (optionalImageVariant.isEmpty()) {
                        return null;
                    }
                    String objectName = imageVariant.getFilePath().replace(String.format("https://%s/%s/", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket()), "");
                    boolean deleted = this.minioS3Service.removeObjectFromS3Bucket(this.s3Properties.getBucket(), objectName);
                    if (!deleted) {
                        log.error("Couldn't delete variant object from S3 bucket: {}", objectName);
                        throw new PixelfreebiesException("Couldn't delete variant object from S3 bucket: " + objectName, INTERNAL_SERVER_ERROR);
                    }
                    return objectName;
                });

        String mainImageObjectName = image.getFilePath().replace(String.format("https://%s/%s/", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket()), "");
        boolean mainImageDeleted = this.minioS3Service.removeObjectFromS3Bucket(this.s3Properties.getBucket(), mainImageObjectName);
        if (!mainImageDeleted) {
            log.error("Couldn't delete image object from S3 bucket: {}", mainImageObjectName);
            throw new PixelfreebiesException("Couldn't delete image object from S3 bucket: " + mainImageObjectName, INTERNAL_SERVER_ERROR);
        }

        this.imageRepository.delete(image);
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
