package com.pixelfreebies.model.dto;

import com.pixelfreebies.model.domain.Image;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * DTO for {@link Image}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of an image file.")
public class ImageDTO implements Serializable {

    @Schema(description = "Unique identifier of the image.", example = "a1dc1968-5ddc-4f4b-80dc-40dec61b7e22")
    private String id;

    @Schema(description = "Title of the image.", example = "AI Brain")
    private String fileTitle;

    @Schema(description = "Path to the image file.", example = "artificial-intelligence-png-11.webp")
    private String filePath;

    @Schema(description = "Content type of the file.", example = "image/png")
    private String contentType;

    @Schema(description = "File size.", example = "42 KB")
    private String size;

    @Schema(description = "Height of the image in pixels.", example = "320")
    private int height;

    @Schema(description = "Width of the image in pixels.", example = "320")
    private int width;

    @Schema(description = "Indicates if the image is active.", example = "true")
    private boolean isActive;

    @Schema(description = "Keywords associated with the image.", example = "technology, AI")
    private String keywords;

    @Schema(description = "Style of the image.", example = "minimalistic")
    private String style;

    @Schema(description = "Indicates if the image supports light mode.", example = "false")
    private boolean isLightMode;

    @Schema(description = "Dominant colors in the image.", example = "[\"#FFFFFF\", \"#000000\"]")
    private Set<String> dominantColors = new HashSet<>();

    @Schema(description = "Number of views.", example = "1000")
    private long viewCount;

    @Schema(description = "Number of downloads.", example = "500")
    private long downloadCount;

    @Schema(description = "Average rating of the image.", example = "4.5")
    private BigDecimal averageRating;

    @Schema(description = "Timestamp of the last download.", example = "2024-01-01T12:00:00")
    private LocalDateTime lastDownloadedAt;

    @Schema(description = "Details of the user who uploaded the image.")
    @ArraySchema(
            schema = @Schema(implementation = UserDTO.class)
    )
    private UserDTO uploadedBy;

    @Schema(description = "Categories associated with the image.")
    @ArraySchema(
            schema = @Schema(implementation = CategoryDTO.class),
            arraySchema = @Schema(description = "Categories associated with the image.")
    )
    private Set<CategoryDTO> categories = new HashSet<>();
}