package com.pixelfreebies.service;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.UpdateImageDTO;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    ImageDTO saveImage(MultipartFile multipartFile, ImageUploadRequest imageUploadRequest) throws IOException;

    Resource loadImageAsResource(String fileId) throws IOException;

    Page<ImageDTO> listAllImages(Pageable pageable);

    void deleteImageById(String fileId);

    ImageDTO updateImage(String imageId, UpdateImageDTO updateImageDTO);

    List<String> searchKeywords(String query, int page, int size);

    Page<ImageDTO> searchImages(String query, PageRequest pageRequest);

    Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable);

}
