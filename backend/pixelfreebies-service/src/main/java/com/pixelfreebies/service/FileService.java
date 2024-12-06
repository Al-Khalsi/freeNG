package com.pixelfreebies.service;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.UpdateImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    ImageDTO storeImage(MultipartFile multipartFile, String fileName,
                        List<String> keywords, List<String> dominantColors,
                        String style, boolean lightMode) throws IOException;

    Resource loadImageAsResource(String fileId) throws IOException;

    List<ImageDTO> listAllImages();

    Page<ImageDTO> listAllImages(Pageable pageable);

    List<ImageDTO> searchImages(String query);

    void deleteImageById(String fileId);

    ImageDTO updateImage(String imageId, UpdateImageDTO updateImageDTO);

    List<String> searchKeywords(String query, int page, int size);

    Page<ImageDTO> searchImages(String query, PageRequest pageRequest);

    Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable);

}
