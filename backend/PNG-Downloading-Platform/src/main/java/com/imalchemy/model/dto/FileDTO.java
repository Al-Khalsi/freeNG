package com.imalchemy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO for {@link com.imalchemy.model.domain.File}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO implements Serializable {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fileTitle;
    private String filePath;
    private String contentType;
    private long size;
    private int height;
    private int width;
    private boolean isActive;
    private String keywords;
    private Set<String> dominantColors;
    private long viewCount;
    private long downloadCount;
    private BigDecimal averageRating;
    private LocalDateTime lastDownloadedAt;

    private UserDTO uploadedBy;
    private Set<CategoryDTO> categories = new HashSet<>();
}