package com.pixelfreebies.util.converter;

import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.KeywordsDTO;
import org.springframework.stereotype.Service;

@Service
public class KeywordsConverter implements Converter<Keywords, KeywordsDTO> {

    @Override
    public Keywords toEntity(KeywordsDTO dto) {
        if (dto == null) return null;

        return Keywords.builder()
                .id(dto.getId())
                .keyword(dto.getKeyword())
                .build();
    }

    @Override
    public KeywordsDTO toDto(Keywords entity) {
        if (entity == null) return null;

        return KeywordsDTO.builder()
                .id(entity.getId())
                .keyword(entity.getKeyword())
                .build();
    }

}
