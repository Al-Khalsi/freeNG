package com.pixelfreebies.service.impl;

import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.repository.KeywordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordValidationService {

    private final KeywordsRepository keywordsRepository;

    public Set<Keywords> validateAndFetchKeywords(List<String> keywords) {
        Set<Keywords> keywordsSet = new HashSet<>();
        List<String> missingKeywords = new ArrayList<>();

        for (String keyword : keywords) {
            Optional<Keywords> optionalKeyword = this.keywordsRepository.findByKeyword(keyword);
            if (optionalKeyword.isPresent()) {
                Keywords kWord = optionalKeyword.get();
                keywordsSet.add(kWord);
            } else missingKeywords.add(keyword);
        }

        if (!missingKeywords.isEmpty()) {
            throw new IllegalArgumentException("The following keywords do not exist: " + String.join(", ", missingKeywords));
        }

        return keywordsSet;
    }

}
