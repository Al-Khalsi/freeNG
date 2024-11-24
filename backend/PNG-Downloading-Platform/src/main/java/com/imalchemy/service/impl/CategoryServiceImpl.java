package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.Image;
import com.imalchemy.model.domain.SubCategory;
import com.imalchemy.model.dto.CategoryDTO;
import com.imalchemy.model.dto.SubCategoryDTO;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.repository.ImageRepository;
import com.imalchemy.repository.SubCategoryRepository;
import com.imalchemy.service.CategoryService;
import com.imalchemy.util.SlugGenerator;
import com.imalchemy.util.converter.CategoryConverter;
import com.imalchemy.util.converter.SubCategoryConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ImageRepository imageRepository;
    private final CategoryConverter categoryConverter;
    private final SubCategoryConverter subCategoryConverter;
    private final SlugGenerator slugGenerator;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Capitalize the category name
        categoryDTO.setName(this.capitalizeCategoryName(categoryDTO.getName()));

        // Determine parent category if provided
        Long parentCategoryId = categoryDTO.getParentId();
        Category parentCategory;
        SubCategory subCategory;

        if (parentCategoryId != null && parentCategoryId > 0) {
            // Fetch the parent category
            parentCategory = this.categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));

            // Create a new SubCategory only if a parent category is provided
            subCategory = new SubCategory();
            subCategory.setName(categoryDTO.getName()); // Set the name for the subcategory
            subCategory.setDescription(categoryDTO.getDescription()); // Set description if needed
            subCategory.setSlug(this.slugGenerator.generateSlug(categoryDTO.getName())); // Generate slug
            subCategory.setParentCategory(parentCategory); // Set the parent category

            // Fetch the next display order for the subcategory
            int nextSubDisplayOrder = this.subCategoryRepository.findMaxDisplayOrder() + 1;
            subCategory.setDisplayOrder(nextSubDisplayOrder);
            subCategory.setParent(false); // This is not a parent category
            subCategory.setLevel(parentCategory.getLevel() + 1); // Set level based on parent
            subCategory.setActive(true);

            // Save the subcategory
            this.subCategoryRepository.save(subCategory);

            // Return the DTO for the subcategory
            return CategoryDTO.builder()
                    .id(subCategory.getId())
                    .name(subCategory.getName())
                    .description(subCategory.getDescription())
                    .iconUrl(subCategory.getIconUrl())
                    .displayOrder(subCategory.getDisplayOrder())
                    .level(subCategory.getLevel())
                    .parentId(subCategory.getId())
                    .isActive(subCategory.isActive())
                    .isParent(subCategory.isParent())
                    .build(); // Return the subcategory DTO directly
        }

        // If no parentId is provided, create a new category
        Category category = this.categoryConverter.toEntity(categoryDTO);
        category.setSlug(this.slugGenerator.generateSlug(category.getName())); // Generate slug for the main category
        int nextCategoryDisplayOrder = this.categoryRepository.findMaxDisplayOrder() + 1; // Fetch the next display order
        category.setDisplayOrder(nextCategoryDisplayOrder);
        category.setParent(true); // This is a parent category
        category.setLevel(0); // Top-level category
        category.setActive(true);

        // Save the main category and return the DTO
        return this.categoryConverter.toDto(this.categoryRepository.save(category));
    }

    private String capitalizeCategoryName(String categoryName) {
        // Split the sentence into words using space as the delimiter
        String[] words = categoryName.split(" ");
        StringBuilder capitalizedSentence = new StringBuilder();

        // Iterate through each word
        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize the first letter and append the rest of the word
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1);
                capitalizedSentence.append(capitalizedWord).append(" ");
            }
        }

        // Trim the final string to remove the trailing space
        return capitalizedSentence.toString().trim();
    }

    @Override
    public String deleteParentCategory(String categoryName) {
        Category foundCategory = this.categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        foundCategory.getSubCategories().forEach(subCategory -> {
            subCategory.setParentCategory(null);
            this.subCategoryRepository.save(subCategory);
            this.subCategoryRepository.flush();

            this.categoryRepository.deleteById(subCategory.getId());
        });
        foundCategory.getImages().forEach(file -> {
            file.setCategories(null);
            this.imageRepository.save(file);
        });
        foundCategory.setImages(null);
        foundCategory.setSubCategories(null);
        this.categoryRepository.save(foundCategory);
        this.categoryRepository.delete(foundCategory);

        return categoryName;
    }

    @Override
    public String deleteSubCategory(String categoryName) {
        Category foundCategory = this.categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        foundCategory.setSubCategories(null);
        foundCategory.getImages().forEach(file -> {
            file.setCategories(null);
            this.imageRepository.save(file);
        });
        foundCategory.setImages(null);
        this.categoryRepository.delete(foundCategory);
        return categoryName;
    }

    @Override
    public List<CategoryDTO> listCategories() {
        return this.categoryRepository.findAll()
                .stream().map(this.categoryConverter::toDto)
                .toList();
    }

    @Override
    public List<SubCategoryDTO> getSubcategories(String parentName) {
        // Fetch the parent category using its name
        Category parentCategory = categoryRepository.findByNameIgnoreCase(parentName).get();

        // Fetch and return the subcategories for the found parent category
        return this.subCategoryRepository.findByParentCategory(parentCategory)
                .stream().map(this.subCategoryConverter::toDto)
                .toList();
    }

    @Override
    public List<CategoryDTO> listParentCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        return allCategories.stream()
                .filter(Category::isParent) // Filter only parent categories
                .map(this.categoryConverter::toDto)
                .toList();
    }

    @Override
    public void assignCategoryToFile(String categoryName, String fileName) {
        Category foundCategory = this.categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        Image foundImage = this.imageRepository.findByFileTitle(fileName)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));

        foundImage.getCategories().add(foundCategory);
        foundCategory.getImages().add(foundImage);
        Integer maxFiles = this.categoryRepository.findMaxFiles();
        foundCategory.setTotalFiles(maxFiles + 1);

        this.categoryRepository.save(foundCategory);
        this.imageRepository.save(foundImage);
    }

}
