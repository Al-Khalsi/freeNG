package com.pixelfreebies.service.keyword;

import com.pixelfreebies.exception.AlreadyExistsException;
import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.util.converter.KeywordsConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordsServiceImpl implements KeywordsService {

    private final KeywordsRepository keywordsRepository;
    private final KeywordsConverter keywordsConverter;

    @Override
    public Page<KeywordsDTO> keywordsPage(PageRequest pageRequest) {
        return this.keywordsRepository.findAll(pageRequest)
                .map(this.keywordsConverter::toDto);
    }

    @Override
    public KeywordsDTO createKeyword(KeywordsDTO keywordsDTO) {
        String[] keywordsArray = keywordsDTO.getKeyword().toLowerCase().split(",");
        List<KeywordsDTO> createdKeywords = new ArrayList<>();

        // Check if any keyword already exists
        for (String keyword : keywordsArray) {
            String trimmedKeyword = keyword.trim().replace(" ", "-");
            Optional<Keywords> optionalKeywords = this.keywordsRepository.findByKeyword(trimmedKeyword);

            if (optionalKeywords.isPresent())
                throw new AlreadyExistsException("Keyword already exists with name: " + trimmedKeyword);
        }

        // If no existing keywords were found, save all of them
        for (String keyword : keywordsArray) {
            String trimmedKeyword = keyword.trim().replace(" ", "-");
            Keywords savedKeyword = this.keywordsRepository.save(this.keywordsConverter.toEntity(new KeywordsDTO(trimmedKeyword)));
            createdKeywords.add(this.keywordsConverter.toDto(savedKeyword));
        }

        return createdKeywords.isEmpty() ? null : createdKeywords.get(0);
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
                .map(keyword -> {
                    log.info("Keyword found: {}", keyword.getKeyword());
                    return this.keywordsConverter.toDto(keyword);
                }).orElseThrow(() -> new NotFoundException("Keyword not found by id: " + keywordId));
    }

}
