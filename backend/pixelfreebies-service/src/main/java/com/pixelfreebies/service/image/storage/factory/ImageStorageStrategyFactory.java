package com.pixelfreebies.service.storage.factory;

import com.pixelfreebies.service.storage.strategy.ImageStorageStrategy;
import com.pixelfreebies.service.storage.strategy.LocalImageStorageStrategy;
import com.pixelfreebies.service.storage.strategy.S3BucketImageStorageStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ImageStorageStrategyFactory {

    private final Map<String, ImageStorageStrategy> strategies;
    
    public ImageStorageStrategyFactory(LocalImageStorageStrategy localImageStorageStrategy,
                                       S3BucketImageStorageStrategy s3BucketImageStorageStrategy) {
        this.strategies = Map.of(
                "dev", localImageStorageStrategy,
                "prod", s3BucketImageStorageStrategy
        );
    }

    public ImageStorageStrategy getStrategy(String environment) {
        return this.strategies.getOrDefault(environment.toLowerCase(),this.strategies.get("dev"));
    }

}
