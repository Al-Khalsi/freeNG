package com.pixelfreebies.config;

import com.pixelfreebies.config.properties.S3Properties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioS3Config {

    private final S3Properties s3Properties;

    @Bean
    public MinioClient minioClient() {
        String endpointUrl = this.s3Properties.getEndpointUrl();
        String accessKey = this.s3Properties.getAccessKey();
        String secretKey = this.s3Properties.getSecretKey();
        try {
            return MinioClient.builder()
                    .endpoint(endpointUrl)
                    .credentials(accessKey, secretKey)
                    .region("us-west-2") // or us-east-1
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Minio client", e);
        }
    }

}
