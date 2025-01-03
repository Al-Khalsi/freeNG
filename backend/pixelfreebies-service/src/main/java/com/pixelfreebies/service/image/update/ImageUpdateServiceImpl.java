package com.pixelfreebies.service.image.update;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.domain.Keywords;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.ImageRemoveDominantColorDTO;
import com.pixelfreebies.model.dto.ImageRemoveKeywordsDTO;
import com.pixelfreebies.model.dto.ImageRemoveStyleDTO;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.repository.KeywordsRepository;
import com.pixelfreebies.service.keyword.KeywordValidationService;
import com.pixelfreebies.util.converter.ImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.pixelfreebies.util.constants.ApplicationConstants.PIXELFREEBIES_SUFFIX;

@Service
@RequiredArgsConstructor
public class ImageUpdateServiceImpl implements ImageUpdateService {

    private final ImageRepository imageRepository;
    private final KeywordsRepository keywordsRepository;
    private final KeywordValidationService keywordValidationService;
    private final ImageConverter imageConverter;

    @Override
    public ImageDTO updateImage(UUID imageId, ImageOperationRequest request) {
        Image image = this.findImageOrThrow(imageId);
        this.updateImageProperties(image, request);

        return this.convertToDto(this.imageRepository.save(image));
    }

    private Image findImageOrThrow(UUID imageId) throws NotFoundException {
        return this.imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found with id " + imageId));
    }

    private void updateImageProperties(Image image, ImageOperationRequest request) {
        if (request.getFileName() != null) {
            image.setFileTitle(request.getFileName() + " " + PIXELFREEBIES_SUFFIX);
        }
        if (request.isLightMode() != image.isLightMode()) {
            image.setLightMode(request.isLightMode());
        }
        if (request.getSource() != null) {
            image.setSource(request.getSource());
        }

        this.updateImageStyles(image, request.getStyle());
        this.updateDominantColors(image, request.getDominantColors());
        this.updateKeywords(image, request.getKeywords());
    }

    private ImageDTO convertToDto(Image image) {
        ImageDTO imageDTO = this.imageConverter.toDto(image);
        String webpImagePath = image.getVariants()
                .stream().findFirst()
                .map(imageVariant -> {
                    if (!imageVariant.getFormat().equals(ImageFormat.WEBP)) return null;
                    return imageVariant.getFilePath();
                }).orElse(image.getFilePath());
        imageDTO.setFilePath(webpImagePath);
        imageDTO.setContentType("image/webp");
        return imageDTO;
    }

    private void updateImageStyles(Image image, List<String> styles) {
        if (styles != null) {
            List<String> currentStyles = image.getStyles();
            styles.forEach(newStyle -> {
                if (!currentStyles.contains(newStyle)) {
                    currentStyles.add(newStyle); // Add only if newStyle doesn't already exist
                }
            });
        }
    }

    private void updateDominantColors(Image image, List<String> dominantColors) {
        if (dominantColors != null) {
            Set<String> currentDominantColors = image.getDominantColors();
            currentDominantColors.addAll(dominantColors); // Since this is a Set, it will only add new dominantColors
        }
    }

    private void updateKeywords(Image image, List<String> keywords) {
        if (keywords != null) {
            Set<Keywords> currentKeywords = image.getKeywords();
            Set<Keywords> newKeywords = this.keywordValidationService.validateAndFetchKeywords(keywords);

            // Add new keywords
            currentKeywords.addAll(newKeywords);
            this.keywordsRepository.saveAll(currentKeywords);
        }
    }

    @Override
    public ImageDTO removeStylesFromImage(UUID imageId, ImageRemoveStyleDTO removeStyleDTO) {
        Image existingImage = this.findImageOrThrow(imageId);

        String styleToRemove = removeStyleDTO.getStyleToRemove();
        if (styleToRemove != null) {
            // Check if any styles to remove exist
            List<String> existingStyles = existingImage.getStyles();
            if (!existingStyles.contains(styleToRemove))
                throw new NotFoundException("The following style was not found: " + styleToRemove);

            // Remove the specified styles
            existingImage.getStyles().remove(styleToRemove);
        }

        return this.convertToDto(this.imageRepository.save(existingImage));
    }

    @Override
    public ImageDTO removeDominantColorsFromImage(UUID imageId, ImageRemoveDominantColorDTO removeColorDTO) {
        Image existingImage = this.findImageOrThrow(imageId);

        String colorToRemove = removeColorDTO.getColorToRemove();
        if (colorToRemove != null) {
            Set<String> currentColors = existingImage.getDominantColors();
            // Find colors that are not present in the image
            if (!currentColors.contains(colorToRemove))
                throw new NotFoundException("The following color was not found: " + colorToRemove);

            // Remove the specified colors
            currentColors.remove(colorToRemove);
        }

        return this.convertToDto(this.imageRepository.save(existingImage));
    }

    @Override
    public ImageDTO removeKeywordsFromImage(UUID imageId, ImageRemoveKeywordsDTO removeKeywordsDTO) {
        Image existingImage = this.findImageOrThrow(imageId);

        Long keywordToRemove = removeKeywordsDTO.getKeywordToRemove();
        if (keywordToRemove != null) {
            Set<Keywords> currentKeywords = existingImage.getKeywords();
            // Check if the keyword exist in database
            Keywords keywords = this.keywordsRepository.findById(keywordToRemove)
                    .orElseThrow(() -> new NotFoundException("Keyword not found with id: " + keywordToRemove));

            // Remove the specified keywords
            currentKeywords.remove(keywords);
        }

        return this.convertToDto(this.imageRepository.save(existingImage));
    }

}
