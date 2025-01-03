package com.pixelfreebies.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Properties {

    private String bucket;
    private String endpointUrl;
    private String accessKey;
    private String secretKey;

}
