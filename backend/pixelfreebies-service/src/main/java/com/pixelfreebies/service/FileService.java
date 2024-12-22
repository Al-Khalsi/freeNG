package com.pixelfreebies.service;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FileService {

    ImageDTO saveImage(MultipartFile multipartFile, ImageUploadRequest imageUploadRequest);

    Page<ImageDTO> listAllImages(Pageable pageable);

    ImageDTO findImageById(UUID fileId);

    void deleteImageById(String fileId);

    List<String> searchKeywords(String query, int page, int size);

    Page<ImageDTO> searchImages(String query, PageRequest pageRequest);

    Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable);

}
