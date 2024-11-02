package com.imalchemy.service.impl;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.File;
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
        Category category = this.categoryConverter.toEntity(categoryDTO);
        // Fetch the next display order from the repository
        int nextDisplayOrder = this.categoryRepository.findMaxDisplayOrder() + 1;

        // Determine parent category if provided
        Category parentCategory = null;
        Long parentCategoryId = categoryDTO.getParentId();
        if (parentCategoryId != null) {
            parentCategory = this.categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
        }
        category.setSlug(this.slugGenerator.generateSlug(category.getName()));
        category.setDisplayOrder(nextDisplayOrder);
        category.setParent(parentCategory);
        category.setLevel(parentCategory != null ? parentCategory.getLevel() + 1 : 0); // Set level based on parent

        // If there's a parent, add this category to its subcategories
        if (parentCategory != null) {
            parentCategory.getSubCategories().add(category);
        }

        return this.categoryConverter.toDto(this.categoryRepository.save(category));
    }

    @Override
    public CategoryDTO updateCategory(String categoryName, CategoryDTO categoryDTO) {
        Category foundCategory = this.categoryRepository.findByName(categoryName)
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
        foundCategory.setParent(parentCategory);

        // If there's a parent, add this category to its subcategories
        if (parentCategory != null) {
            parentCategory.getSubCategories().add(foundCategory);
        }

        return this.categoryConverter.toDto(this.categoryRepository.save(foundCategory));
    }

    @Override
    public String deleteParentCategory(String categoryName) {
        Category foundCategory = this.categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        foundCategory.getSubCategories().forEach(subCategory -> {
            subCategory.setParent(null);
            this.categoryRepository.save(subCategory);
            this.categoryRepository.flush();
            this.categoryRepository.deleteById(subCategory.getId());
        });
        foundCategory.getFiles().forEach(file -> {
            file.setCategories(null);
            this.fileRepository.save(file);
        });
        foundCategory.setFiles(null);
        foundCategory.setSubCategories(null);
        this.categoryRepository.save(foundCategory);
        this.categoryRepository.delete(foundCategory);

        return categoryName;
    }

    @Override
    public String deleteSubCategory(String categoryName) {
        Category foundCategory = this.categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        foundCategory.setParent(null);
        foundCategory.getFiles().forEach(file -> {
            file.setCategories(null);
            this.fileRepository.save(file);
        });
        foundCategory.setFiles(null);
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
        return this.categoryRepository.findByParent_Name(parentName)
                .stream().map(this.categoryConverter::toDto)
                .toList();
    }

    @Override
    public void assignCategoryToFile(String categoryName, String fileName) {
        Category foundCategory = this.categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        File foundFile = this.fileRepository.findByFileTitle(fileName)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));

        foundFile.getCategories().add(foundCategory);
        foundCategory.getFiles().add(foundFile);
        Integer maxFiles = this.categoryRepository.findMaxFiles();
        foundCategory.setTotalFiles(maxFiles + 1);

        this.categoryRepository.save(foundCategory);
        this.fileRepository.save(foundFile);
    }

}
