package com.pixelfreebies.service.storage;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    ImageDTO saveImage(MultipartFile file, ImageOperationRequest request) throws PixelfreebiesException;

    void deleteImage(String imageId) throws NotFoundException;

}
