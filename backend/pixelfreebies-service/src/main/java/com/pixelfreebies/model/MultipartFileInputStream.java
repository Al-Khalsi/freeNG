package com.pixelfreebies.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileInputStream implements MultipartFile {

    private final InputStream inputStream;
    private final long size;
    private final String originalFilename;

    public MultipartFileInputStream(InputStream inputStream, long size, String originalFilename) {
        this.inputStream = inputStream;
        this.size = size;
        this.originalFilename = originalFilename;
    }

    @Override
    public String getName() {
        return "webpImage";
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getContentType() {
        return "image/webp";
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.inputStream.readAllBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream out = new FileOutputStream(dest)) {
            this.inputStream.transferTo(out);
        }
    }

}
