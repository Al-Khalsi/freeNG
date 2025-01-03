package com.pixelfreebies.service.image.query;

import com.pixelfreebies.exception.NotFoundException;
import com.pixelfreebies.model.domain.Image;
import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.repository.ImageRepository;
import com.pixelfreebies.service.keyword.KeywordsService;
import com.pixelfreebies.util.converter.ImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageQueryServiceImpl implements ImageQueryService {

    private final ImageRepository imageRepository;
    private final ImageConverter imageConverter;
    private final KeywordsService keywordsService;

    @Override
    public Page<ImageDTO> listAllImages(Pageable pageable) {
        return this.imageRepository.findAll(pageable)
                .map(this::convertToDto);
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

    @Override
    public ImageDTO findImageById(UUID fileId) throws NotFoundException {
        return this.imageRepository.findById(fileId)
                .map(this.imageConverter::toDto)
                .orElseThrow(() -> new NotFoundException("File not found with id " + fileId));
    }

    @Override
    public Page<ImageDTO> searchImages(String query, PageRequest pageRequest) {
        if (query == null || query.trim().isEmpty()) {
            return Page.empty();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches with pagination
        Page<Image> exactMatches = this.imageRepository.searchFiles(formattedQuery, pageRequest);

        // If exact matches are less than the requested page size, find similar matches
        if (exactMatches.getContent().isEmpty()) {
            return this.imageRepository.searchSimilarFiles(query, pageRequest)
                    .map(this::convertToDto);
        }

        return exactMatches.map(this::convertToDto);
    }

    @Override
    public Page<ImageDTO> listAllImagesByKeywordId(long keywordId, Pageable pageable) {
        KeywordsDTO foundKeyword = this.keywordsService.findKeywordById(keywordId);
        return this.imageRepository.findByKeywords_Id(foundKeyword.getId(), pageable)
                .map(this.imageConverter::toDto);
    }

    @Override
    public List<String> searchKeywords(String query, int page, int size) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Format the query for PostgreSQL full-text search
        String formattedQuery = query.trim().replaceAll("\\s+", " & "); // Replace spaces with AND operator

        // First attempt to find exact matches
        List<String> exactMatches = this.imageRepository.searchKeywords(formattedQuery);
        // If no exact matches found, search for similar entries
        List<String> similarMatches = this.imageRepository.searchSimilarKeywords(query);

        // Create a set of IDs to avoid duplicates
        Set<String> exactMatchIds = new HashSet<>(exactMatches);

        // Add similar matches that are not in exact matches
        List<String> combinedResults = new ArrayList<>(exactMatches);
        similarMatches.stream()
                .filter(keyword -> !exactMatchIds.contains(keyword))
                .forEach(combinedResults::add);

        return combinedResults.stream()
//                .limit(50) // Limit results
                .toList();
    }

}
