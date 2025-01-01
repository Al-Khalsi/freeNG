package com.pixelfreebies.service;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.ImageRemoveDominantColorDTO;
import com.pixelfreebies.model.dto.ImageRemoveKeywordsDTO;
import com.pixelfreebies.model.dto.ImageRemoveStyleDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;

import java.util.UUID;

public interface ImageUpdateService {

    ImageDTO updateImage(UUID imageId, ImageOperationRequest request);

    ImageDTO removeStylesFromImage(UUID imageId, ImageRemoveStyleDTO removeStyleDTO);

    ImageDTO removeDominantColorsFromImage(UUID imageId, ImageRemoveDominantColorDTO removeColorDTO);

    ImageDTO removeKeywordsFromImage(UUID imageId, ImageRemoveKeywordsDTO removeKeywordsDTO);

}
