package com.pixelfreebies.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageStorageStrategy {

    Path store(MultipartFile file, String fileName) throws IOException;

    Resource load(String filePath) throws IOException;

    Path getStorageLocation();

}
