package com.imalchemy.util;

import com.imalchemy.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlugGenerator {

    private final CategoryRepository categoryRepository;

    public String generateSlug(String name) {
        // Convert to lowercase and trim
        String slug = name.toLowerCase().trim();

        // Replace special characters with empty string
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // Replace spaces with single hyphen
        slug = slug.replaceAll("\\s+", "-");

        // Remove multiple consecutive hyphens
        slug = slug.replaceAll("-+", "-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-|-$", "");

        // Ensure uniqueness
        return ensureUniqueSlug(slug);
    }

    private String ensureUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (this.categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}
