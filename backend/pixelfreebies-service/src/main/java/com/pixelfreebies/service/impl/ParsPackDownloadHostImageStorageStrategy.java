package com.pixelfreebies.service.impl;

import com.pixelfreebies.config.FileStorageProperties;
import com.pixelfreebies.service.AbstractBaseImageStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@Profile("prod")
public class ParsPackDownloadHostImageStorageStrategy extends AbstractBaseImageStorageStrategy {

    private final FTPClient ftpClient;

    public ParsPackDownloadHostImageStorageStrategy(FTPClient ftpClient, final FileStorageProperties fileStorageProperties) throws IOException {
        super(fileStorageProperties);
        this.ftpClient = ftpClient;
    }

    @Override
    public Path store(MultipartFile file, String originalFileName) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {

//            String fileUploadLocation = this.env.getProperty("");
//            String remotePath = fileUploadLocation + "/" + originalFileName;
            String remotePath = "/domains/pz20122.parspack.net/public_html/images/" + originalFileName;
            if (this.ftpClient.storeFile(remotePath, inputStream)) {
                Path path = Paths.get(remotePath);
                log.info("Stored file path on ParsPack: {}", path);
                return path;
            } else {
                log.error("Failed to upload file to FTP server (ParsPack)");
                throw new IOException("Failed to upload file to FTP server");
            }

        }
    }

    @Override
    public Resource load(String filePath) throws IOException {
        try {

            // Extract the filename from the filePath
            String fileName = Paths.get(filePath).getFileName().toString();
            // Construct the remote path
            String remotePath = "/domains/pz20122.parspack.net/public_html/images/" + fileName;
            // Create a temporary file to store the downloaded content
            Path tempFile = Files.createTempFile("ftp-download-", fileName);
            // Attempt to retrieve the file
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
                boolean success = ftpClient.retrieveFile(remotePath, fileOutputStream);

                if (success) {
                    // Return a FileSystemResource that can be used for download
                    return new FileSystemResource(tempFile.toFile());
                } else {
                    // Clean up the temp file
                    Files.deleteIfExists(tempFile);
                    log.error("Failed to download file from FTP server: {}", remotePath);
                    throw new IOException("File not found or could not be downloaded: " + fileName);
                }
            }

        } catch (IOException e) {
            log.error("Error loading file from FTP: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Path getStorageLocation() {
        return this.fileStorageLocation;
    }

}
