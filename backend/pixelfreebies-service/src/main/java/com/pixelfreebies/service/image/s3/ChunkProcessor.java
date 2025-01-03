package com.pixelfreebies.service.image.s3;

import com.pixelfreebies.exception.PixelfreebiesException;

import java.io.InputStream;

@FunctionalInterface
public interface ChunkProcessor {

    void process(InputStream inputStream) throws PixelfreebiesException;

}
