package com.imalchemy.service;

import com.imalchemy.model.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(String categoryName, CategoryDTO categoryDTO);

    String deleteParentCategory(String categoryName);

    String deleteSubCategory(String categoryName);

    List<CategoryDTO> listCategories();

    List<CategoryDTO> getSubcategories(String parentName);

    void assignCategoryToFile(String categoryName, String fileName);

    List<CategoryDTO> listParentCategories();

}
