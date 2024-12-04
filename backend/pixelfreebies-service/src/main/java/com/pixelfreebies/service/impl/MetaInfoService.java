package com.pixelfreebies.service.impl;

import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.MetaInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaInfoService {

    public MetaInfo createImageMetaInfoDomain(Image image) {
        return MetaInfo.builder()
                .metaTitle("mmd hassan hammal")
                .description("mmd hassan motavvahem")
                .nameLink(image.getFileTitle())
                .image(image)
                .build();
    }

}
