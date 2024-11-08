package com.imalchemy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.imalchemy.model.domain.Category}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private int displayOrder;
    private int level;
    private boolean isActive;
    private boolean isParent = false;

    private Long parentId;

}