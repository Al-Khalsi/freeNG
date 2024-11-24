package com.imalchemy.service;

import com.imalchemy.model.dto.CategoryDTO;
import com.imalchemy.model.dto.SubCategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    default CategoryDTO updateCategory(String categoryName, CategoryDTO categoryDTO) {
        /*
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

        // If there's a parent, add this category to its subcategories
        if (parentCategory != null) {
            parentCategory.getSubCategories().add(foundCategory);
        }

        return this.categoryConverter.toDto(this.categoryRepository.save(foundCategory));
         */
        return null;
    }

    String deleteParentCategory(String categoryName);

    String deleteSubCategory(String categoryName);

    List<CategoryDTO> listCategories();

    List<SubCategoryDTO> getSubcategories(String parentName);

    void assignCategoryToFile(String categoryName, String fileName);

    List<CategoryDTO> listParentCategories();

}
