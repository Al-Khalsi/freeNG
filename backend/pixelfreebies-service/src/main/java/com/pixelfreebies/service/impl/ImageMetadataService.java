package com.pixelfreebies.service.impl;

import com.luciad.imageio.webp.WebPWriteParam;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.domain.MetaInfo;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.model.enums.ImageUnits;
import com.pixelfreebies.model.enums.Purpose;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.service.ImageStorageStrategy;
import com.pixelfreebies.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageMetadataService {

    private final KeywordsRepository keywordsRepository;
    private final SecurityUtil securityUtil;
    private final ImageStorageStrategy imageStorageStrategy;

    public Image createImageDomain(MultipartFile uploadedMultipartFile, String imageName,
                                   String relativePath, List<String> dominantColors,
                                   String style, boolean lightMode) {
        Image image = new Image();
        image.setFileTitle(imageName);
        image.setFilePath(relativePath);
        image.setContentType(uploadedMultipartFile.getContentType());
        image.setSize(uploadedMultipartFile.getSize()); // size in bytes
        image.setActive(true);
        image.setAverageRating(BigDecimal.ZERO);
        image.setDownloadCount(0);
        image.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        // Calculate dimensions
        calculateDimension(uploadedMultipartFile, image, imageName);
        image.setStyle(style);
        image.setLightMode(lightMode);
        image.getDominantColors().addAll(dominantColors);

        return image;
    }

    public Set<Keywords> createKeywordsDomain(List<String> keywords, Set<Keywords> keywordsSet) {
        for (String keyword : keywords) {
            Optional<Keywords> optionalKeywords = this.keywordsRepository.findByKeyword(keyword);
            if (optionalKeywords.isEmpty())
                keywordsSet.add(Keywords.builder().keyword(keyword).build());
        }
        return keywordsSet;
    }

    public Set<Keywords> validateAndFetchKeywords(List<String> keywords) {
        Set<Keywords> keywordsSet = new HashSet<>();
        List<String> missingKeywords = new ArrayList<>();

        for (String keyword : keywords) {
            Optional<Keywords> optionalKeyword = this.keywordsRepository.findByKeyword(keyword);
            if (optionalKeyword.isPresent()) {
                Keywords kWord = optionalKeyword.get();
                keywordsSet.add(kWord);
            } else missingKeywords.add(keyword);
        }

        if (!missingKeywords.isEmpty()) {
            throw new IllegalArgumentException("The following keywords do not exist: " + String.join(", ", missingKeywords));
        }

        return keywordsSet;
    }

    public MetaInfo createImageMetaInfoDomain(Image image) {
        return MetaInfo.builder()
                .metaTitle("mmd hassan hammal")
                .description("mmd hassan motavvahem")
                .nameLink(image.getFileTitle())
                .image(image)
                .build();
    }

    private void calculateDimension(MultipartFile uploadedMultipartFile, Image imageEntity, String imageName) {
        try {

            BufferedImage image = ImageIO.read(uploadedMultipartFile.getInputStream());
            if (image != null) {
                imageEntity.setWidth(image.getWidth());
                imageEntity.setHeight(image.getHeight());
            } else log.warn("Unable to read image dimensions for imageEntity: {}", imageName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageVariant createImageVariants(MultipartFile uploadedMultipartFile, String relativePath) {
        try {
            // Read original image
            BufferedImage originalImage = ImageIO.read(uploadedMultipartFile.getInputStream());

            // Create WebP variant
            ImageVariant webpVariant = new ImageVariant();
            webpVariant.setFormat(ImageFormat.WEBP);

            // Generate WebP filename
            String webpFileName = generateWebpFileName(relativePath);
            webpVariant.setFilePath(webpFileName);
            webpVariant.setWidth(originalImage.getWidth());
            webpVariant.setHeight(originalImage.getHeight());
            webpVariant.setOriginalImageContentType(uploadedMultipartFile.getContentType());
            webpVariant.setSize(uploadedMultipartFile.getSize());
            webpVariant.setPurpose(Purpose.DOWNLOAD);

            // Convert and save as WebP
            Path webpPath = this.imageStorageStrategy.getStorageLocation().resolve(webpFileName);
            // For lossy compression (smaller file size, some quality loss)
            saveAsWebp(originalImage, new FileOutputStream(webpPath.toFile()), 0.8f, false); // 0.8f is the quality factor (0.0 to 1.0)
            // Or for lossless compression (larger file size, no quality loss)
//            saveAsWebp(originalImage, new FileOutputStream(webpPath.toFile()), 0.0f, true); // 0.8f is the quality factor (0.0 to 1.0)

            return webpVariant;

        } catch (IOException e) {
            log.error("Error creating image variants: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateWebpFileName(String originalPath) {
        // Remove the original extension and append .webp
        return originalPath.replaceFirst("[.][^.]+$", "") + ".webp";
    }

    private void saveAsWebp(BufferedImage image, OutputStream output, float quality, boolean lossless) throws IOException {
        // Convert to TYPE_INT_ARGB if the image has transparency, otherwise TYPE_INT_RGB
        BufferedImage convertedImage = hasTransparency(image)
                ? convertToARGB(image)
                : convertToRGB(image);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!writers.hasNext()) {
            throw new IOException("No WebP writer found");
        }
        ImageWriter writer = writers.next();

        WebPWriteParam writeParam = new WebPWriteParam(Locale.getDefault());
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        if (lossless)
            writeParam.setCompressionType("Lossless");
        else {
            writeParam.setCompressionType("Lossy");
            writeParam.setCompressionQuality(quality);
        }

        try (ImageOutputStream stream = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(stream);
            writer.write(null, new IIOImage(convertedImage, null, null), writeParam);
            stream.flush();
        } finally {
            writer.dispose();
        }
    }

    private boolean hasTransparency(BufferedImage image) {
        return image.getColorModel().hasAlpha();
    }

    private BufferedImage convertToARGB(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_ARGB) {
            return image;
        }
        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    private BufferedImage convertToRGB(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_RGB) {
            return image;
        }
        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public String formatImageSize(long sizeInBytes) {
        if (sizeInBytes <= 0) {
            return "0 Bytes";
        }

        ImageUnits[] units = new ImageUnits[]{
                ImageUnits.BYTES,
                ImageUnits.KB,
                ImageUnits.MB,
                ImageUnits.GB,
        };

        // Start with the first unit (Bytes)
        int unitIndex = 0;
        // Convert to double for precision division
        double size = (double) sizeInBytes;

        // This while loop does the unit conversion:
        // It repeatedly divides the size by 1024 to covert to larger unit
        // It stops when:
        // 1. The size becomes less than 1024, or
        // 2. We've reached the largest unit (GB)
        while (size >= 1024 && unitIndex < units.length - 1) {
            // Divide by 1024 -> covert to next unit
            size /= 1024;
            // Move to the next unit
            unitIndex++;
        }

        // Formatting for KB -> whole numbers
        // Formatting for MB, GB -> 2 decimal precision
        int kbIndexPositionInArray = 1;
        return (unitIndex == kbIndexPositionInArray)
                ? String.format("%d %s", (int) size, units[unitIndex]) // No decimal for KB
                : String.format("%.2f %s", size, units[unitIndex]); // Two decimals for MB and GB
    }

    public void associateImageWithImageVariant(Image image, ImageVariant imageVariant) {
        image.getVariants().add(imageVariant);
        imageVariant.setImage(image);
    }

    public void associateImageWithKeywords(Image image, Set<Keywords> keywordsSet) {
        image.getKeywords().addAll(keywordsSet);
    }

}
