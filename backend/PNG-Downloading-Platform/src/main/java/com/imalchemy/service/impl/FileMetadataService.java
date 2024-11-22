package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.File;
import com.imalchemy.model.enums.ImageUnits;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final CategoryRepository categoryRepository;
    private final SecurityUtil securityUtil;

    public File createFileDomain(MultipartFile uploadedMultipartFile, String fileName,
                                 String relativePath, List<String> dominantColors,
                                 String style) {
        File fileEntity = new File();
        fileEntity.setFileTitle(fileName);
        fileEntity.setFilePath(relativePath);
        fileEntity.setContentType(uploadedMultipartFile.getContentType());
        fileEntity.setSize(uploadedMultipartFile.getSize()); // size in bytes
        fileEntity.setActive(true);
        fileEntity.setAverageRating(BigDecimal.ZERO);
        fileEntity.setDownloadCount(0);
        fileEntity.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        // Calculate dimensions if the fileEntity is an image
        calculateDimension(uploadedMultipartFile, fileEntity, fileName);
        fileEntity.setStyle(style);
        fileEntity.getDominantColors().addAll(dominantColors);
        return fileEntity;
    }

    private void calculateDimension(MultipartFile uploadedMultipartFile, File fileEntity, String fileName) {
        String contentType = uploadedMultipartFile.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            BufferedImage image;
            try {

                image = ImageIO.read(uploadedMultipartFile.getInputStream());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (image != null) {
                fileEntity.setWidth(image.getWidth());
                fileEntity.setHeight(image.getHeight());
            } else log.warn("Unable to read image dimensions for fileEntity: {}", fileName);
        } else {
            log.warn("Uploaded fileEntity is not an image: {}", fileName);
            fileEntity.setHeight(0);
            fileEntity.setWidth(0);
        }
    }

    public String formatFileSize(long sizeInBytes) {
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

    public void associateFileWithCategories(File file, String parentCategoryName, List<String> subCategoryNames) {
        Category parentCategory = this.categoryRepository.findByNameIgnoreCase(parentCategoryName)
                .orElseGet(() -> this.categoryRepository.findByNameIgnoreCase("defaultCategory")
                        .orElseThrow(() -> new IllegalStateException("Default category not found")));

        file.getCategories().add(parentCategory);

        subCategoryNames.stream()
                .filter(name -> !name.isEmpty())
                .map(this.categoryRepository::findByNameIgnoreCase)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(category -> file.getCategories().add(category));
    }

}
