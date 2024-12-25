package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreationService {

    private final SecurityUtil securityUtil;
    private final ImageRepository imageRepository;
    private final ImageValidationService imageValidationService;

    public Image createImageDomain(MultipartFile uploadedMultipartFile, String relativePath, ImageUploadRequest imageUploadRequest) {
        Image image = new Image();
        image.setFileTitle(this.generateImageName(imageUploadRequest.getFileName()));
        image.setFilePath(relativePath);
        image.setContentType(uploadedMultipartFile.getContentType());
        image.setSize(uploadedMultipartFile.getSize()); // size in bytes
        image.setActive(true);
        image.setAverageRating(BigDecimal.ZERO);
        image.setDownloadCount(0);
        image.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        // Calculate dimensions
        this.calculateDimension(uploadedMultipartFile, image, imageUploadRequest.getFileName());
        image.setStyle(imageUploadRequest.getStyle());
        image.setLightMode(imageUploadRequest.isLightMode());

        String source = imageUploadRequest.getSource();
        if (source == null || source.trim().isEmpty()) source = "PixelFreebies";
        image.setSource(source);

        if (imageUploadRequest.getDominantColors() != null) {
            image.getDominantColors().addAll(imageUploadRequest.getDominantColors());
        }

        return image;
    }

    public String generateImageName(String originalName) {
        // Remove file extension if present
        String baseName = originalName.contains(".")
                ? originalName.substring(0, originalName.lastIndexOf("."))
                : originalName;

        // Capitalize words and add "Pixelfreebies" suffix
        String capitalizedName = this.imageValidationService.capitalizeWords(baseName);
        String nameWithSuffix = capitalizedName + " Pixelfreebies";

        // Check if name exists and add random number if needed
        String finalName = nameWithSuffix;
        while (this.imageRepository.existsByFileTitle(finalName)) {
            int random = new Random().nextInt(1000);
            finalName = nameWithSuffix + " " + random;
        }

        return finalName;
    }

    public String generateImagePath(String fileName) {
        // Convert to lowercase and replace spaces with hyphens
        return fileName.toLowerCase().trim().replace(" ", "-");
    }

    private void calculateDimension(MultipartFile uploadedMultipartFile, Image imageEntity, String imageName) throws PixelfreebiesException {
        try {

            BufferedImage image = ImageIO.read(uploadedMultipartFile.getInputStream());
            if (image != null) {
                imageEntity.setWidth(image.getWidth());
                imageEntity.setHeight(image.getHeight());
            } else log.warn("Unable to read image dimensions for imageEntity: {}", imageName);

        } catch (IOException e) {
            log.error("error calculating image dimension: {}", e.getMessage());
            throw new PixelfreebiesException("error calculating image dimension: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
