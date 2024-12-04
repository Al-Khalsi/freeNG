package com.pixelfreebies.service.impl;

import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.service.ImageStorageStrategy;
import com.pixelfreebies.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreationService {

    private final SecurityUtil securityUtil;

    public Image createImageDomain(MultipartFile uploadedMultipartFile, String imageName,
                                   String relativePath, List<String> dominantColors,
                                   String style, boolean lightMode) {
        Image image = new Image();
        image.setFileTitle(imageName);
        image.setFilePath(relativePath);
        image.setContentType(uploadedMultipartFile.getContentType());
        image.setSize(uploadedMultipartFile.getSize()); // size in bytes
        image.setActive(true);
        image.setAverageRating(BigDecimal.ZERO);
        image.setDownloadCount(0);
        image.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        // Calculate dimensions
        calculateDimension(uploadedMultipartFile, image, imageName);
        image.setStyle(style);
        image.setLightMode(lightMode);
        image.getDominantColors().addAll(dominantColors);

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
