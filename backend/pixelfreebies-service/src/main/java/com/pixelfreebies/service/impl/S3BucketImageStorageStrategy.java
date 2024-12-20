package com.pixelfreebies.service.impl;

import com.luciad.imageio.webp.WebPWriteParam;
import com.pixelfreebies.config.properties.FileStorageProperties;
import com.pixelfreebies.config.properties.S3Properties;
import com.pixelfreebies.service.AbstractBaseImageStorageStrategy;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Locale;

@Slf4j
@Service
@Profile("prod")
public class S3BucketImageStorageStrategy extends AbstractBaseImageStorageStrategy {

    private final MinioS3Service minioS3Service;
    private final S3Properties s3Properties;
    private final MinioClient minioClient;

    public S3BucketImageStorageStrategy(final FileStorageProperties fileStorageProperties, MinioS3Service minioS3Service, S3Properties s3Properties, MinioClient minioClient) throws IOException {
        super(fileStorageProperties);
        this.minioS3Service = minioS3Service;
        this.s3Properties = s3Properties;
        this.minioClient = minioClient;
    }

    @Override
    public Path store(MultipartFile multipartFile, String imageName) {
        try {
            // https://c567062.parspack.net/c567062//api.png --> if in the root home folder, after the bucket name i.e., c567062 comes "//"
            // https://c567062.parspack.net/c567062/images/api.png --> if in another folder, after the bucket name i.e., c567062 comes "/"
            String remotePath = "/images/" + imageName;
            boolean result = this.minioS3Service.storeMultipartFileToS3Bucket(this.s3Properties.getBucket(), remotePath, multipartFile);

            if (!result) throw new RuntimeException("Error uploading multipartFile");
            Path path = Paths.get(remotePath);
            log.info("Stored multipartFile path on ParsPack S3: {}", path);

            return path;
        } catch (Exception e) {
            log.error("Failed to upload image on S3 bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Error uploading file: " + e.getMessage());
        }
    }

    @Override
    public boolean supportsWebP() {
        return true;
    }

    // upload to ftp
    /*@Override
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
    }*/
    @Override
    public void storeWebp(BufferedImage image, String remotePath, float quality, boolean lossless) {
        // Normalize path for S3 compatibility (if needed)
        String normalizedRemotePath = remotePath.replace("\\", "/");
        log.info("Normalized S3 path: {}", normalizedRemotePath);

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

            // Upload to S3 bucket using MinIO
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(this.s3Properties.getBucket())
                            .object(normalizedRemotePath)
                            .stream(inputStream, byteArrayOutputStream.size(), -1)
                            .contentType("image/webp")
                            .build()
            );

            // Verify the upload by checking if the object exists
            this.minioClient.statObject(StatObjectArgs.builder()
                    .bucket(this.s3Properties.getBucket())
                    .object(normalizedRemotePath)
                    .build());

            log.info("WebP image successfully uploaded to S3: {}", normalizedRemotePath);

        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error storing WebP image: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
    }

    @Override
    public Resource load(String filePath) {
        return null;
    }

    @Override
    public Path getStorageLocation() {
        return Paths.get("images");
    }

}
