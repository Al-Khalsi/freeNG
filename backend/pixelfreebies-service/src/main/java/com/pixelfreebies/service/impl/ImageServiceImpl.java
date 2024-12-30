package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.properties.S3Properties;
import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.ImageRemoveDominantColorDTO;
import com.pixelfreebies.model.dto.ImageRemoveStyleDTO;
import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.repository.ImageVariantRepository;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.service.ImageService;
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
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageVariantRepository imageVariantRepository;
    private final KeywordsRepository keywordsRepository;
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
    public ImageDTO saveImage(MultipartFile uploadedMultipartFile, ImageOperationRequest imageOperationRequest) {
        try {
            // Generate image name with suffix and possible random number
            String generatedImageName = this.imageCreationService.generateImageName(imageOperationRequest.getFileName());
            // Generate path-friendly name
            String pathName = this.imageCreationService.generateImagePath(generatedImageName);
            // Validate the generated name
            this.imageValidationService.validateImageName(pathName);

            String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = pathName + fileExtension;
            Path relativePath = this.imageStorageStrategy.store(uploadedMultipartFile, newFileName);

            // Validate keywords and retrieve their entities
            Set<Keywords> keywordsSet = this.keywordValidationService.validateAndFetchKeywords(imageOperationRequest.getKeywords());

            // Create the domains
            Image image = this.imageCreationService.createImageDomain(uploadedMultipartFile, relativePath.toString(), imageOperationRequest);
            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(uploadedMultipartFile, relativePath.toString());

            // Set full paths
            String normalizedPngPath = relativePath.toString().replace("\\", "/");
            String pngPath = this.getFullPath(normalizedPngPath);
            image.setFilePath(pngPath);
            log.debug("Normalized path (png) for saving image: {}", normalizedPngPath);
            log.debug("Full Path (png): {}", pngPath);

            String normalizedWebpFilePath = imageVariant.getFilePath().replace("\\", "/");
            String webpPath = this.getFullPath(normalizedWebpFilePath);
            imageVariant.setFilePath(webpPath);
            log.debug("Normalized path (webp) for saving image: {}", normalizedWebpFilePath);
            log.debug("Full Path (webp): {}", webpPath);

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
        image.getVariants().forEach(imageVariant -> {
            Optional<ImageVariant> optionalImageVariant = this.imageVariantRepository.findById(imageVariant.getId());
            if (optionalImageVariant.isPresent()) {
                String objectName = imageVariant.getFilePath().replace(String.format("https://%s/%s/", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket()), "");
                boolean deleted = this.minioS3Service.removeObjectFromS3Bucket(this.s3Properties.getBucket(), objectName);
                if (!deleted) {
                    log.error("Couldn't delete variant object from S3 bucket: {}", objectName);
                    throw new PixelfreebiesException("Couldn't delete variant object from S3 bucket: " + objectName, INTERNAL_SERVER_ERROR);
                }
            }
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

    @Override
    public ImageDTO updateImage(UUID imageId, ImageOperationRequest imageOperationRequest) {
        Image image = this.imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));

        String imageName = imageOperationRequest.getFileName();
        boolean lightMode = imageOperationRequest.isLightMode();
        String source = imageOperationRequest.getSource();
        List<String> dominantColors = imageOperationRequest.getDominantColors();
        List<String> keywords = imageOperationRequest.getKeywords();
        List<String> styles = imageOperationRequest.getStyle();

        if (imageName != null) image.setFileTitle(imageName + " Pixelfreebies");
        if (lightMode != image.isLightMode()) image.setLightMode(lightMode);
        if (source != null) image.setSource(source);

        // Handle styles
        if (styles != null) {
            List<String> currentStyles = image.getStyles();
            styles.forEach(newStyle -> {
                if (!currentStyles.contains(newStyle)) currentStyles.add(newStyle); // Add only if a newStyle doesn't already exist
            });
        }

        // Handle dominantColors
        if (dominantColors != null) {
            Set<String> currentDominantColors = image.getDominantColors();
            currentDominantColors.addAll(dominantColors); // Since this is a Set, it'll only add new dominantColors
        }

        // Handle keywords
        if (keywords != null) {
            Set<Keywords> currentKeywords = image.getKeywords();
            Set<Keywords> newKeywords = this.keywordValidationService.validateAndFetchKeywords(keywords);

            // Add new keywords
            currentKeywords.addAll(newKeywords);
            this.keywordsRepository.saveAll(currentKeywords);
        }

        // Save the updated image
        Image updatedImage = this.imageRepository.save(image);
        return this.convertToDto(updatedImage);
    }

    @Override
    public ImageDTO removeStylesFromImage(UUID imageId, ImageRemoveStyleDTO imageRemoveStyleDTO) {
        Image existingImage = this.imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));

        List<String> stylesToRemove = imageRemoveStyleDTO.getStylesToRemove();
        if (stylesToRemove != null) {
            // Check if any styles to remove exist
            List<String> existingStyles = existingImage.getStyles();
            List<String> notFoundStyles = stylesToRemove.stream()
                    .filter(style -> !existingStyles.contains(style))
                    .toList();

            if (!notFoundStyles.isEmpty())
                throw new NotFoundException("The following styles were not found: " + notFoundStyles);

            // Remove the specified styles
            existingImage.getStyles().removeAll(stylesToRemove);
        }

        return this.convertToDto(this.imageRepository.save(existingImage));
    }

    @Override
    public ImageDTO removeDominantColorsFromImage(UUID imageId, ImageRemoveDominantColorDTO removeColorDTO) {
        Image existingImage = this.imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));

        Set<String> colorsToRemove = removeColorDTO.getColorsToRemove();
        if (colorsToRemove != null) {
            Set<String> currentColors = existingImage.getDominantColors();
            // Find colors that are not present
            Set<String> notFoundColors = colorsToRemove.stream()
                    .filter(color -> !currentColors.contains(color))
                    .collect(Collectors.toSet());

            if (!notFoundColors.isEmpty())
                throw new NotFoundException("The following colors were not found: " + notFoundColors);

            // Remove the specified colors
            currentColors.removeAll(colorsToRemove);
        }

        return this.convertToDto(this.imageRepository.save(existingImage));
    }

}
