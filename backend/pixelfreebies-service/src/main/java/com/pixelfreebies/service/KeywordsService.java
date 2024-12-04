package com.pixelfreebies.service;

import com.pixelfreebies.model.dto.KeywordsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface KeywordsService {

    Page<KeywordsDTO> keywordsPage(PageRequest pageRequest);

    KeywordsDTO createKeyword(KeywordsDTO keywordsDTO);

    KeywordsDTO updateKeyword(long keywordId, KeywordsDTO keywordsDTO);

    void deleteKeyword(long keywordId);

}
