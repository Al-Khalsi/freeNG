package com.pixelfreebies.service;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.dto.ImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ImageQueryService {

    Page<ImageDTO> listAllImages(Pageable pageable);

    ImageDTO findImageById(UUID fileId) throws NotFoundException;

    Page<ImageDTO> searchImages(String query, PageRequest pageRequest);

    Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable);

    List<String> searchKeywords(String query, int page, int size);

}