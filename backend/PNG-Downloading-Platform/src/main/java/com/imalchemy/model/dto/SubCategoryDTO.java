package com.imalchemy.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.imalchemy.model.domain.SubCategory}
 */
@Data
@Builder
public class SubCategoryDTO implements Serializable {

    private Long id;

    private String name;
    private String description;
    private String iconUrl;
    private int displayOrder;
    private int level;
    private boolean isActive;
    private boolean isParent;

    private Long parentId;

    private CategoryDTO parentCategory;
    private Set<ImageDTO> images;

}