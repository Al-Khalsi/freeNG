package com.pixelfreebies.model.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadRequest {

    private String fileName;
    private List<String> keywords;
    private List<String> dominantColors;
    private String style;
    private boolean lightMode;

}
