package com.pixelfreebies.util.converter;

import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.model.dto.RoleDTO;
import com.pixelfreebies.model.dto.UserDTO;
import com.pixelfreebies.service.impl.ImageMetadataService;
import com.pixelfreebies.service.impl.ImageValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ImageConverter implements Converter<Image, ImageDTO> {

    private final RoleConverter roleConverter;
    private final ImageMetadataService imageMetadataService;
    private final ImageValidationService imageValidationService;

    @Override
    public Image toEntity(ImageDTO dto) {
        if (dto == null) return null;

        Set<Keywords> keywordsSet = new HashSet<>();
        for (KeywordsDTO keywordDTO : dto.getKeywords()) {
            Keywords newKeyword = Keywords.builder().keyword(keywordDTO.getKeyword()).build();
            keywordsSet.add(newKeyword);
        }

        Image image = Image.builder()
                .fileTitle(dto.getFileTitle())
                .filePath(dto.getFilePath())
                .contentType(dto.getContentType())
                .size(Long.parseLong(dto.getSize()))
                .height(dto.getHeight())
                .width(dto.getWidth())
                .isActive(dto.isActive())
                .keywords(keywordsSet)
                .styles(dto.getStyle())
                .source(dto.getSource())
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

        return image;
    }

    @Override
    public ImageDTO toDto(Image entity) {
        if (entity == null) return null;

        String cleanedTitle = this.imageValidationService.cleanDisplayName(entity.getFileTitle());
        ImageDTO imageDTO = ImageDTO.builder()
                .id(entity.getId().toString())
                .fileTitle(cleanedTitle)
                .filePath(entity.getFilePath())
                .contentType(entity.getContentType())
                .size(this.imageMetadataService.formatImageSize(entity.getSize()))
                .height(entity.getHeight())
                .width(entity.getWidth())
                .isActive(entity.isActive())
                .style(entity.getStyles())
                .source(entity.getSource())
                .isLightMode(entity.isLightMode())
                .dominantColors(entity.getDominantColors())
                .viewCount(entity.getViewCount())
                .downloadCount(entity.getDownloadCount())
                .averageRating(entity.getAverageRating())
                .lastDownloadedAt(entity.getLastDownloadedAt())
                .build();

        // Handle keywords if present
        Set<Keywords> keywords = entity.getKeywords();
        if (!keywords.isEmpty()) {
            Set<KeywordsDTO> keywordsDTOSet = new HashSet<>();
            for (Keywords keyword : keywords) {
                KeywordsDTO keywordsDTO = new KeywordsDTO();
                keywordsDTO.setId(keyword.getId());
                keywordsDTO.setKeyword(keyword.getKeyword());

                keywordsDTOSet.add(keywordsDTO);
            }
            imageDTO.setKeywords(keywordsDTOSet);
        }

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

        return imageDTO;
    }

}
