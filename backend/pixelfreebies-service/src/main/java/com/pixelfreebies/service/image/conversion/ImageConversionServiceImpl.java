package com.pixelfreebies.service.image.conversion;

import com.pixelfreebies.config.properties.S3Properties;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.service.image.processor.ImageProcessingService;
import com.pixelfreebies.service.image.s3.MinioS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageConversionServiceImpl implements ImageConversionService {

    private final MinioS3Service minioS3Service;
    private final ImageProcessingService imageProcessingService;
    private final S3Properties s3Properties;

    @Override
    public String convertAndUploadImage(String objectName, String targetFormat) throws PixelfreebiesException, IOException {
        // Download the image as an InputStream
        InputStream imageStream = this.minioS3Service.downloadObject(this.s3Properties.getBucket(), objectName);

        // Process the image and convert it to the target format
        InputStream convertedImage = this.imageProcessingService.convertImage(imageStream, targetFormat);

        // Generate a new file name for the converted image
        String convertedFileName = this.generateConvertedFileName(objectName, targetFormat);

        // Upload the converted image back to MinIO
        this.minioS3Service.uploadObject(this.s3Properties.getBucket(), convertedFileName, convertedImage, "image/" + targetFormat);

        return convertedFileName;
    }

    private String generateConvertedFileName(String originalName, String targetFormat) {
        String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
        return baseName + "." + targetFormat;
    }

}
