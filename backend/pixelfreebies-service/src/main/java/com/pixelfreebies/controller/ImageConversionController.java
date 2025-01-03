package com.pixelfreebies.controller;

import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.service.image.conversion.ImageConversionService;
import com.pixelfreebies.service.image.s3.MinioS3Service;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/image")
@SecurityRequirement(name = "BearerToken")
@Tag(name = "Image Conversion API", description = "Endpoints for Image convert operations")
public class ImageConversionController {

    private final ImageConversionService imageConversionService;

    @PostMapping("/convert")
    public ResponseEntity<String> convertImage(@RequestParam("bucket") String bucketName,
                                               @RequestParam("object") String objectName,
                                               @RequestParam("format") String targetFormat) {
        try {
            String convertedFileName = this.imageConversionService.convertAndUploadImage(bucketName, objectName, targetFormat);

            return ResponseEntity.ok("Image converted and uploaded successfully as " + convertedFileName);

        } catch (Exception e) {
            log.error("Error converting image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting image: " + e.getMessage());
        }
    }

}
