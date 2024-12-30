package com.pixelfreebies.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ImageRemoveDominantColorDTO {

    private Set<String> colorsToRemove;

}
