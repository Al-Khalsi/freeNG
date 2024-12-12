package com.pixelfreebies.model.dto;

import com.pixelfreebies.model.domain.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO for {@link Image}
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of an image file.")
public class ImageDTO implements Serializable {

    private String id;
    private String fileTitle;
    private String filePath;
    private String contentType;
    private String size;
    private int height;
    private int width;
    private boolean isActive;
    @Builder.Default
    private Set<KeywordsDTO> keywords = new HashSet<>();
    private String style;
    private boolean isLightMode;
    @Builder.Default
    private Set<String> dominantColors = new HashSet<>();
    private long viewCount;
    private long downloadCount;
    private BigDecimal averageRating;
    private LocalDateTime lastDownloadedAt;
    private UserDTO uploadedBy;

}