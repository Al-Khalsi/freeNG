package com.pixelfreebies.model.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageOperationRequest {

    private String fileName;
    private List<String> keywords;
    private List<String> dominantColors;
    private List<String> style;
    private boolean lightMode;
    private String source;

    private String environment; // environment indicates on which storage environment we want to save the image e.g., dev, prod

}
