package com.pixelfreebies.util.converter;

import com.pixelfreebies.model.domain.Category;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.model.dto.CategoryDTO;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.RoleDTO;
import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.service.impl.ImageMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ImageConverter implements Converter<Image, ImageDTO> {

    private final CategoryConverter categoryConverter;
    private final RoleConverter roleConverter;
    private final ImageMetadataService imageMetadataService;

    @Override
    public Image toEntity(ImageDTO dto) {
        if (dto == null) return null;

        Image image = Image.builder()
                .fileTitle(dto.getFileTitle())
                .filePath(dto.getFilePath())
                .contentType(dto.getContentType())
                .size(Long.parseLong(dto.getSize()))
                .height(dto.getHeight())
                .width(dto.getWidth())
                .isActive(dto.isActive())
                .keywords(dto.getKeywords())
                .style(dto.getStyle())
                .isLightMode(dto.isLightMode())
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
            image.setUploadedBy(uploadedBy);
        }

        // Handle categories if present
        if (dto.getCategories() != null) {
            Set<Category> categories = dto.getCategories().stream()
                    .map(this.categoryConverter::toEntity)
                    .collect(Collectors.toSet());
            image.setCategories(categories);
        }

        return image;
    }

    @Override
    public ImageDTO toDto(Image entity) {
        if (entity == null) return null;

        ImageDTO imageDTO = ImageDTO.builder()
                .id(entity.getId().toString())
                .fileTitle(entity.getFileTitle())
                .filePath(entity.getFilePath())
                .contentType(entity.getContentType())
                .size(this.imageMetadataService.formatImageSize(entity.getSize()))
                .height(entity.getHeight())
                .width(entity.getWidth())
                .isActive(entity.isActive())
                .keywords(entity.getKeywords())
                .style(entity.getStyle())
                .isLightMode(entity.isLightMode())
                .dominantColors(entity.getDominantColors())
                .viewCount(entity.getViewCount())
                .downloadCount(entity.getDownloadCount())
                .averageRating(entity.getAverageRating())
                .lastDownloadedAt(entity.getLastDownloadedAt())
                .build();

        // Handle uploadedBy user if present
        User uploadedBy = entity.getUploadedBy();
        if (uploadedBy != null) {
            UserDTO uploadedByDTO = new UserDTO();
            uploadedByDTO.setId(uploadedBy.getId()); // Set the user ID from entity
            uploadedByDTO.setEmail(uploadedBy.getEmail());
            uploadedByDTO.setUsername(uploadedBy.getUsername());
            Set<RoleDTO> roleDTOS = uploadedBy.getRoles().stream().map(this.roleConverter::toDto).collect(Collectors.toSet());
            uploadedByDTO.setRoles(roleDTOS);

            imageDTO.setUploadedBy(uploadedByDTO);
        }

        // Handle categories if present
        if (entity.getCategories() != null) {
            Set<CategoryDTO> categoryDTOS = entity.getCategories().stream()
                    .map(this.categoryConverter::toDto)
                    .collect(Collectors.toSet());
            imageDTO.setCategories(categoryDTOS);
        }

        return imageDTO;
    }

}
