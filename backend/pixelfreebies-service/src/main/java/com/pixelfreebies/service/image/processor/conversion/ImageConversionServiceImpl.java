package com.pixelfreebies.service.image.processor.conversion;

import com.pixelfreebies.config.properties.S3Properties;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.service.image.processor.ImageProcessingService;
import com.pixelfreebies.service.image.s3.MinioS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageConversionServiceImpl implements ImageConversionService {

    private final MinioS3Service minioS3Service;
    private final ImageProcessingService imageProcessingService;
    private final S3Properties s3Properties;

    /**
     * Converts and uploads an image in chunks to manage memory usage efficiently.
     *
     * @param objectName   the name of the object to convert.
     * @param targetFormat the format to be converted to.
     * @return the name of the converted object.
     * @throws PixelfreebiesException .
     * @throws IOException            .
     */
    @Override
    public String convertAndUploadImageInChunks(String objectName, String targetFormat) throws PixelfreebiesException, IOException {
        // Create a temporary output stream for the converted image
        try (PipedInputStream pipedInput = new PipedInputStream();
             PipedOutputStream pipedOutput = new PipedOutputStream(pipedInput)) {

            // Start a separate thread to process and convert the image in chunks.
            Thread processingThread = this.getImageConversionInChunksProcessingThread(this.s3Properties.getBucket(), objectName, targetFormat, pipedOutput);

            // Generate a new file name for the converted image
            String convertedFileName = this.generateConvertedFileName(objectName, targetFormat);

            // Upload the converted image back to MinIO
            this.minioS3Service.uploadObjectToS3Bucket(this.s3Properties.getBucket(), convertedFileName, pipedInput, "image/" + targetFormat);

            // Wait for the processing thread to complete.
            processingThread.join();

            return convertedFileName;
        } catch (InterruptedException e) {
            throw new PixelfreebiesException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    // Creates a thread to handle chunk-based image processing and conversion.
    private @NotNull Thread getImageConversionInChunksProcessingThread(String bucketName, String objectName, String targetFormat, PipedOutputStream pipedOutput) {
        Thread processingThread = new Thread(() -> {
            try (pipedOutput) {
                // Process the S3 object in chunks and convert it.
                this.minioS3Service.processObjectInChunksFromS3Bucket(bucketName, objectName, inputStream -> {
                    try {
                        this.imageProcessingService.convertImageInChunks(inputStream, pipedOutput, targetFormat);
                    } catch (IOException e) {
                        throw new PixelfreebiesException("Error during image processing: " + e, INTERNAL_SERVER_ERROR);
                    }
                });
            } catch (Exception e) {
                log.error("Error in processing thread: {}", e.getMessage());
            }
        });
        processingThread.start();
        return processingThread;
    }

    // Generates a new file name based on the original name and target format.
    private String generateConvertedFileName(String originalName, String targetFormat) {
        String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
        return baseName + "." + targetFormat;
    }

}
