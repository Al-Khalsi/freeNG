package com.pixelfreebies.service.impl;

import com.luciad.imageio.webp.WebPWriteParam;
import com.pixelfreebies.config.FileStorageProperties;
import com.pixelfreebies.service.AbstractBaseImageStorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;

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
            String remotePath = "/domains/pz20122.parspack.net/public_html/images/" + originalFileName;
            if (ftpClient.storeFile(remotePath, inputStream)) {
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
    public boolean supportsWebP() {
        return true;
    }

    @Override
    public void storeWebp(BufferedImage image, String remotePath, float quality, boolean lossless) throws IOException {
        // Normalize path for FTP compatibility
        String normalizedRemotePath = remotePath.replace("\\", "/");
        log.info("Normalized FTP path: {}", normalizedRemotePath);

        // Prepare the WebP image output to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageWriter writer = null;

        try {
            // Find a suitable writer for WebP format
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
            if (!writers.hasNext()) {
                throw new IOException("No WebP writer found");
            }
            writer = writers.next();

            WebPWriteParam writeParam = new WebPWriteParam(Locale.getDefault());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(lossless ? "Lossless" : "Lossy");
            if (!lossless) {
                writeParam.setCompressionQuality(quality);
            }

            // Write the image to the ByteArrayOutputStream
            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                writer.setOutput(imageOutputStream);
                writer.write(null, new IIOImage(image, null, null), writeParam);
            }

            // Convert ByteArrayOutputStream to InputStream
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            // Store the file using storeFile
            boolean success = this.ftpClient.storeFile(normalizedRemotePath, inputStream);
            if (!success) {
                int replyCode = this.ftpClient.getReplyCode();
                String replyString = this.ftpClient.getReplyString();
                log.error("Failed to upload file to FTP server. Reply Code: {}, Reply String: {}", replyCode, replyString);
                throw new IOException("Failed to upload file to FTP server: " + replyString);
            }

            log.info("WebP image successfully transferred to {}", normalizedRemotePath);

        } catch (IOException e) {
            log.error("Error storing WebP image: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (writer != null) {
                writer.dispose();
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
        // Path object representing the root path for FTP storage
        return Paths.get("/domains/pz20122.parspack.net/public_html/images/");
    }

}
