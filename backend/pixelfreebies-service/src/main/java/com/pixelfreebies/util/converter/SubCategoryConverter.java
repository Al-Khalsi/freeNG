package com.pixelfreebies.util.converter;

import com.pixelfreebies.model.domain.SubCategory;
import com.pixelfreebies.model.dto.SubCategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubCategoryConverter implements Converter<SubCategory, SubCategoryDTO> {

    @Override
    public SubCategory toEntity(SubCategoryDTO dto) {
        if (dto == null) return null;

        return SubCategory.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .iconUrl(dto.getIconUrl())
                .isActive(true)
                .isParent(dto.isParent())
                .displayOrder(dto.getDisplayOrder())
                .level(dto.getLevel())
                .build();
    }

    @Override
    public SubCategoryDTO toDto(SubCategory entity) {
        if (entity == null) return null;

        return SubCategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .displayOrder(entity.getDisplayOrder())
                .level(entity.getLevel())
                .parentId(entity.getId())
                .isActive(entity.isActive())
                .isParent(entity.isParent())
                .build();
    }

}
