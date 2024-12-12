package com.pixelfreebies.service.impl;

import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import com.pixelfreebies.service.ImageCreationService;
import com.pixelfreebies.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@Profile("!prod")
@RequiredArgsConstructor
public class ImageCreationServiceDev implements ImageCreationService {

    private final SecurityUtil securityUtil;

    @Override
    public Image createImageDomain(MultipartFile uploadedMultipartFile,
                                   String relativePath, ImageUploadRequest imageUploadRequest) {
        log.info("Creating image domain for file: {} with relative path: {}", imageUploadRequest.getFileName(), relativePath);
        Image image = new Image();
        image.setFileTitle(imageUploadRequest.getFileName());
        image.setFilePath(relativePath);
        image.setContentType(uploadedMultipartFile.getContentType());
        image.setSize(uploadedMultipartFile.getSize()); // size in bytes
        image.setActive(true);
        image.setAverageRating(BigDecimal.ZERO);
        image.setDownloadCount(0);
        image.setUploadedBy(this.securityUtil.getAuthenticatedUser());

        // Calculate dimensions
        calculateDimension(uploadedMultipartFile, image, imageUploadRequest.getFileName());
        image.setStyle(imageUploadRequest.getStyle());
        image.setLightMode(imageUploadRequest.isLightMode());

        String source = imageUploadRequest.getSource();
        if (source == null || source.trim().isEmpty()) source = "PixelFreebies";
        image.setSource(source);

        image.getDominantColors().addAll(imageUploadRequest.getDominantColors());

        log.info("Image domain created: {}", image);
        return image;
    }

    private void calculateDimension(MultipartFile uploadedMultipartFile, Image imageEntity, String imageName) {
        log.info("Calculating dimensions for image: {}", imageName);
        try {
            BufferedImage image = ImageIO.read(uploadedMultipartFile.getInputStream());
            if (image != null) {
                imageEntity.setWidth(image.getWidth());
                imageEntity.setHeight(image.getHeight());
                log.info("Dimensions set - Width: {}, Height: {}", image.getWidth(), image.getHeight());
            } else {
                log.warn("Unable to read image dimensions for imageEntity: {}", imageName);
            }
        } catch (IOException e) {
            log.error("Error calculating dimensions for image: {}", imageName, e);
            throw new RuntimeException(e);
        }
    }

}
