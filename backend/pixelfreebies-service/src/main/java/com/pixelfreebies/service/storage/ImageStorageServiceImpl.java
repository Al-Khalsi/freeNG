package com.pixelfreebies.service.storage;

import com.pixelfreebies.config.properties.S3Properties;
import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.repository.ImageVariantRepository;
import com.pixelfreebies.service.ImageStorageService;
import com.pixelfreebies.service.ImageStorageStrategy;
import com.pixelfreebies.service.impl.*;
import com.pixelfreebies.util.converter.ImageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private final ImageRepository imageRepository;
    private final ImageVariantRepository imageVariantRepository;
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageCreationService imageCreationService;
    private final ImageMetadataService imageMetadataService;
    private final ImageValidationService imageValidationService;
    private final KeywordValidationService keywordValidationService;
    private final MinioS3Service minioS3Service;
    private final S3Properties s3Properties;
    private final ImageConverter imageConverter;

    @Override
    public ImageDTO saveImage(MultipartFile file, ImageOperationRequest request) throws PixelfreebiesException {
        try {
            String newFileName = this.validateAndGenerateImageName(file, request);
            Path relativePath = this.imageStorageStrategy.store(file, newFileName);

            // Validate keywords and create image domain
            Set<Keywords> keywordsSet = this.keywordValidationService.validateAndFetchKeywords(request.getKeywords());
            Image image = this.imageCreationService.createImageDomain(file, relativePath.toString(), request);
            ImageVariant imageVariant = this.imageMetadataService.createImageVariants(file, relativePath.toString());

            // Set paths and associations
            this.setImagePaths(relativePath, image, imageVariant);
            this.imageMetadataService.associateImageWithImageVariant(image, imageVariant);
            this.imageMetadataService.associateImageWithKeywords(image, keywordsSet);

            return this.saveImageAndVariant(image, imageVariant);
        } catch (IOException e) {
            log.error("Failed to save image: {}", e.getMessage());
            throw new PixelfreebiesException(e.getMessage(), BAD_REQUEST);
        }
    }

    @Override
    public void deleteImage(String imageId) throws NotFoundException {
        Image image = this.imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));

        this.deleteImageVariantsFromS3(image);
        this.deleteMainImageFromS3(image);
        this.imageRepository.delete(image);
    }

    // Helper methods...
    private @NotNull String validateAndGenerateImageName(MultipartFile uploadedMultipartFile, ImageOperationRequest imageOperationRequest) throws IOException {
        // Generate image name with suffix and possible random number
        String generatedImageName = this.imageCreationService.generateImageName(imageOperationRequest.getFileName());
        // Generate path-friendly name
        String pathName = this.imageCreationService.generateImagePath(generatedImageName);
        // Validate the generated name
        this.imageValidationService.validateImageName(pathName);

        String originalFileName = Objects.requireNonNull(uploadedMultipartFile.getOriginalFilename());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        return pathName + fileExtension; // new file name
    }

    private void setImagePaths(Path relativePath, Image image, ImageVariant imageVariant) {
        String normalizedPath = relativePath.toString().replace("\\", "/");
        String fullPath = this.getFullPath(normalizedPath);

        image.setFilePath(fullPath);
        imageVariant.setFilePath(fullPath);

        log.debug("Normalized path for saving image: {}", normalizedPath);
        log.debug("Full Path for image and variant: {}", fullPath);
    }

    public @NotNull String getFullPath(String objectName) {
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return String.format("https://%s/%s/%s", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket(), objectName);
    }

    private ImageDTO saveImageAndVariant(Image image, ImageVariant imageVariant) {
        // Cascades save to associated entities
        Image savedImage = this.imageRepository.save(image);
        //this.imageVariantRepository.save(imageVariant);

        return this.imageConverter.toDto(savedImage);
    }

    private void deleteImageVariantsFromS3(Image image) throws PixelfreebiesException {
        for (ImageVariant imageVariant : image.getVariants()) {
            Optional<ImageVariant> optionalImageVariant = this.imageVariantRepository.findById(imageVariant.getId());
            if (optionalImageVariant.isPresent()) {
                String objectName = imageVariant.getFilePath().replace(String.format("https://%s/%s/", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket()), "");
                boolean deleted = this.minioS3Service.removeObjectFromS3Bucket(this.s3Properties.getBucket(), objectName);
                if (!deleted) {
                    log.error("Couldn't delete variant object from S3 bucket: {}", objectName);
                    throw new PixelfreebiesException("Couldn't delete variant object from S3 bucket: " + objectName, INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    private void deleteMainImageFromS3(Image image) throws PixelfreebiesException {
        String mainImageObjectName = image.getFilePath().replace(String.format("https://%s/%s/", this.s3Properties.getEndpointUrl(), this.s3Properties.getBucket()), "");
        boolean mainImageDeleted = this.minioS3Service.removeObjectFromS3Bucket(this.s3Properties.getBucket(), mainImageObjectName);
        if (!mainImageDeleted) {
            log.error("Couldn't delete image object from S3 bucket: {}", mainImageObjectName);
            throw new PixelfreebiesException("Couldn't delete image object from S3 bucket: " + mainImageObjectName, INTERNAL_SERVER_ERROR);
        }
    }

}
