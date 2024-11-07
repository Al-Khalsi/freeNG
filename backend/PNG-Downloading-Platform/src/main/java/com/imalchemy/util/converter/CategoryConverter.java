package com.imalchemy.util.converter;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConverter implements Converter<Category, CategoryDTO> {

    @Override
    public Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;

        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .iconUrl(dto.getIconUrl())
                .isActive(true)
                .displayOrder(dto.getDisplayOrder())
                .level(dto.getLevel())
                .build();
    }

    @Override
    public CategoryDTO toDto(Category entity) {
        if (entity == null) return null;

        long parentId = 0;
        if (entity.getParent() != null)
            parentId = entity.getParent().getId();

        return CategoryDTO.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .displayOrder(entity.getDisplayOrder())
                .level(entity.getLevel())
                .parentId(parentId)
                .isActive(entity.isActive())
                .build();
    }

}
