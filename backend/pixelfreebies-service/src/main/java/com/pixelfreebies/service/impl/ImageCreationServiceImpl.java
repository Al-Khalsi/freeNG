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
@Profile("prod")
@RequiredArgsConstructor
public class ImageCreationServiceImpl implements ImageCreationService {

    private final SecurityUtil securityUtil;

    @Override
    public Image createImageDomain(MultipartFile uploadedMultipartFile,
                                   String relativePath, ImageUploadRequest imageUploadRequest) {
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
        String source = imageUploadRequest.getSource() != null
                ? imageUploadRequest.getSource()
                : "pixelfreebies.com";
        image.setSource(source);
        image.getDominantColors().addAll(imageUploadRequest.getDominantColors());

        return image;
    }

    private void calculateDimension(MultipartFile uploadedMultipartFile, Image imageEntity, String imageName) {
        try {

            BufferedImage image = ImageIO.read(uploadedMultipartFile.getInputStream());
            if (image != null) {
                imageEntity.setWidth(image.getWidth());
                imageEntity.setHeight(image.getHeight());
            } else log.warn("Unable to read image dimensions for imageEntity: {}", imageName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
