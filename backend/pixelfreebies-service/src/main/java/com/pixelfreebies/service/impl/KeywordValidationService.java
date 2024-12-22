package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.repository.KeywordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordValidationService {

    private final KeywordsRepository keywordsRepository;

    public Set<Keywords> validateAndFetchKeywords(List<String> keywords) throws PixelfreebiesException {
        Set<Keywords> keywordsSet = new HashSet<>();
        List<String> missingKeywords = new ArrayList<>();

        if (keywords == null) {
            return keywordsSet;
        }

        for (String keyword : keywords) {
            Optional<Keywords> optionalKeyword = this.keywordsRepository.findByKeyword(keyword);
            if (optionalKeyword.isPresent()) {
                Keywords kWord = optionalKeyword.get();
                keywordsSet.add(kWord);
            } else missingKeywords.add(keyword);
        }

        if (!missingKeywords.isEmpty()) {
            throw new PixelfreebiesException("The following keywords do not exist: " + String.join(", ", missingKeywords), INTERNAL_SERVER_ERROR);
        }

        return keywordsSet;
    }

}
