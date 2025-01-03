package com.pixelfreebies.service.image.storage.strategy;

import com.pixelfreebies.exception.PixelfreebiesException;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageStorageStrategy {

    Path store(MultipartFile file, String fileName) throws PixelfreebiesException, IOException;

    default boolean supportsWebP() {
        return false;
    }

    default void storeWebp(BufferedImage image, String remotePath, float quality, boolean lossless) throws PixelfreebiesException {
        throw new UnsupportedOperationException("This strategy does not support WebP storage");
    }

    Path getStorageLocation();

}
