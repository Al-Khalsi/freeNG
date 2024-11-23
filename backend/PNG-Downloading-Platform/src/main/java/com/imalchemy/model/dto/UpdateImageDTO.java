package com.imalchemy.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdateImageDTO {

    private String fileTitle;
    private String keywords;
    private String style;
    private Set<String> dominantColors;
    private BigDecimal averageRating;
    private boolean active;
    private boolean lightMode;

}
