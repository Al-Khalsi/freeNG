package com.pixelfreebies.service;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    ImageDTO saveImage(MultipartFile multipartFile, ImageOperationRequest imageOperationRequest);

    Page<ImageDTO> listAllImages(Pageable pageable);

    ImageDTO findImageById(UUID fileId);

    void deleteImageById(String fileId) throws PixelfreebiesException;

    List<String> searchKeywords(String query, int page, int size);

    Page<ImageDTO> searchImages(String query, PageRequest pageRequest);

    Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable);

    ImageDTO updateImage(UUID imageId, ImageOperationRequest imageOperationRequest);

}
