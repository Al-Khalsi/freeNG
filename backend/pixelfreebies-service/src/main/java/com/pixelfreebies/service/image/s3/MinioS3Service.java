package com.pixelfreebies.service.image.s3;

import com.pixelfreebies.exception.PixelfreebiesException;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioS3Service {

    private final MinioClient minioClient;

    public boolean uploadObjectToS3Bucket(String bucketName, String desiredFilename, MultipartFile multipartFile) throws PixelfreebiesException {
        try {
            // upload the multipart file to s3
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(desiredFilename)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build());

            // verify the upload by checking if the object exists
            this.minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(desiredFilename)
                    .build());

            return true; // If no exception was thrown, the object exists

        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Error uploading object to S3 bucket: {}", e.getMessage());
            throw new PixelfreebiesException("Error uploading object: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    public long getFileSize(InputStream inputStream) throws IOException {
        long size = 0;
        byte[] buffer = new byte[1024]; // 1KB buffer
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            size += bytesRead;
        }

        return size;
    }

    public void uploadObject(String bucketName, String objectName, InputStream inputStream, String contentType) throws PixelfreebiesException {
        try {
            this.minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, -1, 10485760)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("Error uploading object to S3 bucket: {}", e.getMessage());
            throw new PixelfreebiesException("Error uploading object: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    public InputStream downloadObject(String bucketName, String objectName) throws PixelfreebiesException {
        try {
            return this.minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("Error downloading object from S3 bucket: {}", e.getMessage());
            throw new PixelfreebiesException("Error downloading object: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    public boolean removeObjectFromS3Bucket(String bucketName, String objectName) throws PixelfreebiesException {
        try {
            // remove the object
            this.minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            // Verify deletion by checking if the object exists
            try {
                minioClient.statObject(StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
                // If statObject does not throw an exception, the object still exists
                log.error("Object was not deleted: {}", objectName);
                throw new PixelfreebiesException("Object was not deleted: " + objectName, INTERNAL_SERVER_ERROR);
            } catch (MinioException e) {
                // If statObject throws an exception, the object does not exist, which means deletion was successful
                log.debug("Object deleted successfully: {}", objectName);
                return true;
            }

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error removing object from S3 bucket: {}", e.getMessage());
            throw new PixelfreebiesException("Error deleting object: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

}
