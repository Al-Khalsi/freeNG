package com.pixelfreebies.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageStorageStrategy {

    Path store(MultipartFile file, String fileName) throws IOException;

    default boolean supportsWebP() {
        return false;
    }

    default void storeWebp(BufferedImage image, String remotePath, float quality, boolean lossless) throws IOException {
        throw new UnsupportedOperationException("This strategy does not support WebP storage");
    }

    Resource load(String filePath) throws IOException;

    Path getStorageLocation();

}
