package com.pixelfreebies.service;

import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ImageCreationService {

    Image createImageDomain(MultipartFile uploadedMultipartFile, String relativePath, ImageUploadRequest imageUploadRequest);

}
