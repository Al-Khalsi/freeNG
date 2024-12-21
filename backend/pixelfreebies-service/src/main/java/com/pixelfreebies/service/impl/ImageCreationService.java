package com.pixelfreebies.service.impl;

import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import com.pixelfreebies.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreationService {

    private final SecurityUtil securityUtil;

    public Image createImageDomain(MultipartFile uploadedMultipartFile,
                                   String relativePath, ImageUploadRequest imageUploadRequest) {
        Image image = new Image();
        image.setFileTitle(this.capitalizeFirstLetters(imageUploadRequest.getFileName()));
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

        if (imageUploadRequest.getDominantColors() != null) {
            image.getDominantColors().addAll(imageUploadRequest.getDominantColors());
        }

        return image;
    }

    public String capitalizeFirstLetters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split(" "); // Split the string into words
        StringBuilder capitalizedString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) { // Check if the word is not empty
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase(); // Capitalize first letter
                capitalizedString.append(capitalizedWord).append(" "); // Append the capitalized word
            }
        }

        return capitalizedString.toString().trim();
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
