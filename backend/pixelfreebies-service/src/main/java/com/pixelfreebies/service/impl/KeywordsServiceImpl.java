package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.service.KeywordsService;
import com.pixelfreebies.util.converter.KeywordsConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordsServiceImpl implements KeywordsService {

    private static final Logger log = LoggerFactory.getLogger(KeywordsServiceImpl.class);
    private final KeywordsRepository keywordsRepository;
    private final KeywordsConverter keywordsConverter;

    @Override
    public Page<KeywordsDTO> keywordsPage(PageRequest pageRequest) {
        return this.keywordsRepository.findAll(pageRequest)
                .map(this.keywordsConverter::toDto);
    }

    @Override
    public KeywordsDTO createKeyword(KeywordsDTO keywordsDTO) {
        Keywords savedKeyword = this.keywordsRepository.save(this.keywordsConverter.toEntity(keywordsDTO));
        return this.keywordsConverter.toDto(savedKeyword);
    }

    @Override
    public KeywordsDTO updateKeyword(long keywordId, KeywordsDTO keywordsDTO) {
        Keywords foundKeywords = this.keywordsRepository.findById(keywordId)
                .orElseThrow(() -> new NotFoundException("Keyword not found"));
        foundKeywords.setKeyword(keywordsDTO.getKeyword());
        Keywords savedKeyword = this.keywordsRepository.save(foundKeywords);

        return this.keywordsConverter.toDto(savedKeyword);
    }

    @Override
    public void deleteKeyword(long keywordId) {
        this.keywordsRepository.deleteById(keywordId);
    }

    @Override
    public KeywordsDTO findKeywordById(long keywordId) {
        return this.keywordsRepository.findById(keywordId)
//                .map(this.keywordsConverter::toDto)
                .map(keyword -> {
                    log.info("Keyword found: {}", keyword.getKeyword());
                    return this.keywordsConverter.toDto(keyword);
                }).orElseThrow(()-> new NotFoundException("Keyword not found by id: " + keywordId));
    }

}
