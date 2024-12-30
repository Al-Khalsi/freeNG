package com.pixelfreebies.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ImageRemoveKeywordsDTO {

    private Set<Long> keywordsToRemove;

}
