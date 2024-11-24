package com.imalchemy.repository;

import com.imalchemy.model.domain.Category;
import com.imalchemy.model.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    Optional<SubCategory> findByNameIgnoreCase(String s);

    List<SubCategory> findByParentCategory(Category parentCategory);

    // The query uses `COALESCE` to return 0 if there are no categories yet, which prevents a null result.
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM Category c")
    int findMaxDisplayOrder();

}