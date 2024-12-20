package com.pixelfreebies.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class MinioS3Service {

    private final MinioClient minioClient;

    public void uploadFile(String bucketName, String desiredFilename, MultipartFile file) throws Exception {
        try {
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(desiredFilename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw new Exception("Error uploading file: " + e.getMessage());
        }
    }

}
