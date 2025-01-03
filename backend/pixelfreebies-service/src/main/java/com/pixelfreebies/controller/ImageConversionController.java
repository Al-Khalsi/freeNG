package com.pixelfreebies.controller;

import com.pixelfreebies.service.image.processor.conversion.ImageConversionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/image")
@SecurityRequirement(name = "BearerToken")
@Tag(name = "Image Conversion API", description = "Endpoints for Image convert operations")
public class ImageConversionController {

    private final ImageConversionService imageConversionService;

    @PostMapping("/convert")
    public ResponseEntity<String> convertImage(@RequestParam("object") String objectName,
                                               @RequestParam("format") String targetFormat) throws IOException {
        String convertedFileName = this.imageConversionService.convertAndUploadImage(objectName, targetFormat);
        return ResponseEntity.ok("Image converted and uploaded successfully as " + convertedFileName);
    }

}
