package com.pixelfreebies.service.image.core;

import com.luciad.imageio.webp.WebPWriteParam;
import com.pixelfreebies.exception.PixelfreebiesException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.ImageVariant;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.model.enums.ImageUnits;
import com.pixelfreebies.model.enums.Purpose;
import com.pixelfreebies.service.storage.strategy.ImageStorageStrategy;
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
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageMetadataService {

    public ImageVariant createImageVariants(MultipartFile uploadedMultipartFile, String relativePath, ImageStorageStrategy imageStorageStrategy) throws PixelfreebiesException {
        try {
            // Read original image
            BufferedImage originalImage = ImageIO.read(uploadedMultipartFile.getInputStream());

            // Create WebP variant
            ImageVariant webpVariant = new ImageVariant();
            webpVariant.setFormat(ImageFormat.WEBP);

            // Generate WebP filename
            relativePath = relativePath.replace("\\images\\png\\", "\\images\\webp\\");
            String webpFileName = this.generateWebpFileName(relativePath);
            webpVariant.setFilePath(webpFileName);
            webpVariant.setWidth(originalImage.getWidth());
            webpVariant.setHeight(originalImage.getHeight());
            webpVariant.setOriginalImageContentType(uploadedMultipartFile.getContentType());
            webpVariant.setSize(uploadedMultipartFile.getSize());
            webpVariant.setPurpose(Purpose.DOWNLOAD);

            if (imageStorageStrategy.supportsWebP())
                imageStorageStrategy.storeWebp(originalImage, webpFileName, 0.8f, false);
            else {
                // Default fallback: Save WebP locally
                Path webpPath = imageStorageStrategy.getStorageLocation().resolve(webpFileName);
                saveAsWebp(originalImage, new FileOutputStream(webpPath.toFile()), 0.8f, false);
            }

            return webpVariant;

        } catch (IOException e) {
            log.error("Error creating image variants: {}", e.getMessage());
            throw new PixelfreebiesException("Error creating image variant: " + e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    private String generateWebpFileName(String originalPath) {
        // Remove the original extension and append .webp
        return originalPath.replaceFirst("[.][^.]+$", "") + ".webp";
    }

    private void saveAsWebp(BufferedImage image, OutputStream output, float quality, boolean lossless) throws IOException, PixelfreebiesException {
        // Convert to TYPE_INT_ARGB if the image has transparency, otherwise TYPE_INT_RGB
        BufferedImage convertedImage = hasTransparency(image)
                ? convertToARGB(image)
                : convertToRGB(image);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!writers.hasNext()) {
            throw new PixelfreebiesException("No WebP writer found", INTERNAL_SERVER_ERROR);
        }
        ImageWriter writer = writers.next();

        WebPWriteParam writeParam = new WebPWriteParam(Locale.getDefault());
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionType(lossless ? "Lossless" : "Lossy");
        if (!lossless) {
            writeParam.setCompressionQuality(quality);
        }

        try (ImageOutputStream stream = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(stream);
            writer.write(null, new IIOImage(convertedImage, null, null), writeParam);
            stream.flush();
        } catch (IOException e) {
            log.error("Error saving image webp: {}", e.getMessage());
            throw new PixelfreebiesException("Error saving image webp: " + e.getMessage(), INTERNAL_SERVER_ERROR);
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
        if (keywordsSet.isEmpty()) return;
        image.getKeywords().addAll(keywordsSet);
    }

}
