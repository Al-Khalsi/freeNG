package com.imalchemy.util.converter;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.File;
import com.imalchemy.model.domain.User;
import com.imalchemy.model.dto.CategoryDTO;
import com.imalchemy.model.dto.FileDTO;
import com.imalchemy.model.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileConverter implements Converter<File, FileDTO> {

    private final CategoryConverter categoryConverter;

    @Override
    public File toEntity(FileDTO dto) {
        if (dto == null) return null;

        File file = File.builder()
                .fileTitle(dto.getFileTitle())
                .filePath(dto.getFilePath())
                .contentType(dto.getContentType())
                .size(dto.getSize())
                .height(dto.getHeight())
                .width(dto.getWidth())
                .isActive(dto.isActive())
                .keywords(dto.getKeywords())
                .dominantColors(dto.getDominantColors())
                .viewCount(dto.getViewCount())
                .downloadCount(dto.getDownloadCount())
                .averageRating(dto.getAverageRating())
                .lastDownloadedAt(dto.getLastDownloadedAt())
                .build();

        // Handle uploadedBy user if present
        if (dto.getUploadedBy() != null) {
            User uploadedBy = new User(); // Assuming you have a User entity class
            uploadedBy.setId(dto.getUploadedBy().getId()); // Set the user ID from DTO
            file.setUploadedBy(uploadedBy);
        }

        // Handle categories if present
        if (dto.getCategories() != null) {
            Set<Category> categories = dto.getCategories().stream()
                    .map(this.categoryConverter::toEntity)
                    .collect(Collectors.toSet());
            file.setCategories(categories);
        }

        return file;
    }

    @Override
    public FileDTO toDto(File entity) {
        if (entity == null) return null;

        FileDTO fileDTO = FileDTO.builder()
                .createdAt(entity.getCreatedAt()) // Assuming you have a createdAt field in DTO
                .updatedAt(entity.getUpdatedAt()) // Assuming you have an updatedAt field in DTO
                .fileTitle(entity.getFileTitle())
                .filePath(entity.getFilePath())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .height(entity.getHeight())
                .width(entity.getWidth())
                .isActive(entity.isActive())
                .keywords(entity.getKeywords())
                .dominantColors(entity.getDominantColors())
                .viewCount(entity.getViewCount())
                .downloadCount(entity.getDownloadCount())
                .averageRating(entity.getAverageRating())
                .lastDownloadedAt(entity.getLastDownloadedAt())
                .build();

        // Handle uploadedBy user if present
        if (entity.getUploadedBy() != null) {
            UserDTO uploadedByDTO = new UserDTO();
            uploadedByDTO.setId(entity.getUploadedBy().getId()); // Set the user ID from entity
            fileDTO.setUploadedBy(uploadedByDTO);
        }

        // Handle categories if present
        if (entity.getCategories() != null) {
            Set<CategoryDTO> categoryDTOS = entity.getCategories().stream()
                    .map(this.categoryConverter::toDto)
                    .collect(Collectors.toSet());
            fileDTO.setCategories(categoryDTOS);
        }

        return fileDTO;
    }

}
