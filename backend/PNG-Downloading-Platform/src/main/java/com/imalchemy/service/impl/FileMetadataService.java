package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.File;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final CategoryRepository categoryRepository;
    private final SecurityUtil securityUtil;

    public File createFileDomain(MultipartFile multipartFile, String fileName,
                                 String relativePath, List<String> dominantColors,
                                 String style) {
        File file = new File();
        file.setFileTitle(fileName);
        file.setFilePath(relativePath);
        file.setContentType(multipartFile.getContentType());
        file.setSize(multipartFile.getSize());
        file.setActive(true);
        file.setAverageRating(BigDecimal.ZERO);
        file.setDownloadCount(0);
        file.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        file.setHeight(0);
        file.setWidth(0);
        file.setStyle(style);
        file.getDominantColors().addAll(dominantColors);
        return file;
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
