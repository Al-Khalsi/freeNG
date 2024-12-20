package com.pixelfreebies.service;

import com.pixelfreebies.config.properties.S3Properties;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioS3Service {

    private final MinioClient minioClient;
    private final S3Properties s3Properties;

    public void uploadFile(String bucketName, String objectName, MultipartFile file) throws Exception {
        try {
            bucketName = this.s3Properties.getBucket();

            ObjectWriteResponse objectWriteResponse = this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            log.info("response: {}", objectWriteResponse.object().isEmpty());
        } catch (Exception e) {
            throw new Exception("Error uploading file: " + e.getMessage());
        }
    }

}
