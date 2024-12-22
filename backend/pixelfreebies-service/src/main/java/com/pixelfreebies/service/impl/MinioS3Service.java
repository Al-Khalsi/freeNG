package com.pixelfreebies.service.impl;

import com.pixelfreebies.exception.PixelfreebiesException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class MinioS3Service {

    private final MinioClient minioClient;

    public boolean storeMultipartFileToS3Bucket(String bucketName, String desiredFilename, MultipartFile multipartFile) throws PixelfreebiesException {
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
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(desiredFilename)
                    .build());

            return true; // If no exception was thrown, the object exists
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new PixelfreebiesException("Error uploading multipartFile: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

}
