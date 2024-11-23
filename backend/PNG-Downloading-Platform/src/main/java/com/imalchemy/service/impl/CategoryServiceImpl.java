package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.Image;
import com.imalchemy.model.dto.CategoryDTO;
import com.imalchemy.repository.CategoryRepository;
import com.imalchemy.repository.FileRepository;
import com.imalchemy.service.CategoryService;
import com.imalchemy.util.SlugGenerator;
import com.imalchemy.util.converter.CategoryConverter;
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
    private final CategoryConverter categoryConverter;
    private final SlugGenerator slugGenerator;
    private final FileRepository fileRepository;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        categoryDTO.setName(this.capitalizeCategoryName(categoryDTO.getName()));
        Category category = this.categoryConverter.toEntity(categoryDTO);
        // Fetch the next display order from the repository
        int nextDisplayOrder = this.categoryRepository.findMaxDisplayOrder() + 1;

        // Determine parent category if provided
        Category parentCategory = null;
        Long parentCategoryId = categoryDTO.getParentId();
        if (parentCategoryId != null && parentCategoryId > 0) {
            parentCategory = this.categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
        }
        category.setSlug(this.slugGenerator.generateSlug(category.getName()));
        category.setDisplayOrder(nextDisplayOrder);
        category.setParentCategory(parentCategory);
        category.setParent(true);
        category.setLevel(parentCategory != null ? parentCategory.getLevel() + 1 : 0); // Set level based on parent

        // If there's a parent, add this category to its subcategories
        if (parentCategory != null) {
            category.setParent(false);
            parentCategory.getSubCategories().add(category);
        }

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
    public CategoryDTO updateCategory(String categoryName, CategoryDTO categoryDTO) {
        Category foundCategory = this.categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        foundCategory.setName(categoryDTO.getName());
        foundCategory.setDescription(categoryDTO.getDescription());
        foundCategory.setIconUrl(categoryDTO.getIconUrl());
        foundCategory.setActive(categoryDTO.isActive());
        foundCategory.setDisplayOrder(categoryDTO.getDisplayOrder());
        foundCategory.setLevel(categoryDTO.getLevel());
        foundCategory.setSlug(this.slugGenerator.generateSlug(categoryDTO.getName()));

        // Determine parent category if provided
        Category parentCategory = null;
        Long parentCategoryId = categoryDTO.getParentId();
        if (parentCategoryId != null) {
            parentCategory = this.categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
        }
        foundCategory.setParentCategory(parentCategory);

        // If there's a parent, add this category to its subcategories
        if (parentCategory != null) {
            parentCategory.getSubCategories().add(foundCategory);
        }

        return this.categoryConverter.toDto(this.categoryRepository.save(foundCategory));
    }

    @Override
    public String deleteParentCategory(String categoryName) {
        Category foundCategory = this.categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        foundCategory.getSubCategories().forEach(subCategory -> {
            subCategory.setParentCategory(null);
            this.categoryRepository.save(subCategory);
            this.categoryRepository.flush();
            this.categoryRepository.deleteById(subCategory.getId());
        });
        foundCategory.getImages().forEach(file -> {
            file.setCategories(null);
            this.fileRepository.save(file);
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
        foundCategory.setParentCategory(null);
        foundCategory.getImages().forEach(file -> {
            file.setCategories(null);
            this.fileRepository.save(file);
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
    public List<CategoryDTO> getSubcategories(String parentName) {
        // Fetch the parent category using its name
        Category parentCategory = categoryRepository.findByNameIgnoreCase(parentName).get();

        // Fetch and return the subcategories for the found parent category
        return categoryRepository.findByParentCategory(parentCategory)
                .stream().map(this.categoryConverter::toDto)
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
        Image foundImage = this.fileRepository.findByFileTitle(fileName)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));

        foundImage.getCategories().add(foundCategory);
        foundCategory.getImages().add(foundImage);
        Integer maxFiles = this.categoryRepository.findMaxFiles();
        foundCategory.setTotalFiles(maxFiles + 1);

        this.categoryRepository.save(foundCategory);
        this.fileRepository.save(foundImage);
    }

}
