package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.Image;
import com.imalchemy.model.domain.ImageVariant;
import com.imalchemy.model.enums.ImageFormat;
import com.imalchemy.model.enums.ImageUnits;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final CategoryRepository categoryRepository;
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
        // Create variants (e.g., WebP)
        createImageVariants(image, uploadedMultipartFile, relativePath);

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

    public void createImageVariants(Image image, MultipartFile uploadedMultipartFile, String relativePath) {
        // Here you would convert the image to different formats and create ImageVariant objects
        try {

            BufferedImage originalImage = ImageIO.read(uploadedMultipartFile.getInputStream());

            // webp variant
            ImageVariant webpVariant = new ImageVariant();
            webpVariant.setImage(image);
            webpVariant.setFormat(ImageFormat.WEBP);
            webpVariant.setFilePath(relativePath);
            webpVariant.setWidth(originalImage.getWidth());
            webpVariant.setHeight(originalImage.getHeight());

            // Convert and save WebP
            saveAsWebP(originalImage, webpVariant.getFilePath());

            image.getVariants().add(webpVariant);

        } catch (IOException e) {
            log.error("Error creating image variants: {}", e.getMessage());
        }
    }

    private void saveAsWebP(BufferedImage image, String filePath) throws IOException {
        // Create output stream to save the WebP file
        try (ImageOutputStream output = ImageIO.createImageOutputStream(Files.newOutputStream(Path.of(filePath)))) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
            writer.setOutput(output);
            writer.write(image);
            writer.dispose();
        }
    }

    public String formatImageSize(long sizeInBytes) {
        if (sizeInBytes <= 0) {
            return "0 Bytes";
        }

        ImageUnits[] units = new ImageUnits[]{
                ImageUnits.BYTES,
                ImageUnits.KB,
                ImageUnits.MB,
                ImageUnits.GB,
        };

        // Start with the first unit (Bytes)
        int unitIndex = 0;
        // Convert to double for precision division
        double size = (double) sizeInBytes;

        // This while loop does the unit conversion:
        // It repeatedly divides the size by 1024 to covert to larger unit
        // It stops when:
        // 1. The size becomes less than 1024, or
        // 2. We've reached the largest unit (GB)
        while (size >= 1024 && unitIndex < units.length - 1) {
            // Divide by 1024 -> covert to next unit
            size /= 1024;
            // Move to the next unit
            unitIndex++;
        }

        // Formatting for KB -> whole numbers
        // Formatting for MB, GB -> 2 decimal precision
        int kbIndexPositionInArray = 1;
        return (unitIndex == kbIndexPositionInArray)
                ? String.format("%d %s", (int) size, units[unitIndex]) // No decimal for KB
                : String.format("%.2f %s", size, units[unitIndex]); // Two decimals for MB and GB
    }

    public void associateImageWithCategories(Image image, String parentCategoryName, List<String> subCategoryNames) {
        Category parentCategory = this.categoryRepository.findByNameIgnoreCase(parentCategoryName)
                .orElseGet(() -> this.categoryRepository.findByNameIgnoreCase("defaultCategory")
                        .orElseThrow(() -> new IllegalStateException("Default category not found")));

        image.getCategories().add(parentCategory);

        subCategoryNames.stream()
                .filter(name -> !name.isEmpty())
                .map(this.categoryRepository::findByNameIgnoreCase)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(category -> image.getCategories().add(category));
    }

}
