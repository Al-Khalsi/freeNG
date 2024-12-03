package com.pixelfreebies.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateImageDTO {

    private String fileTitle;
    private Set<String> keywords = new HashSet<>();
    private String style;
    private Set<String> dominantColors = new HashSet<>();
    private BigDecimal averageRating;
    private boolean active;
    private boolean lightMode;

}
