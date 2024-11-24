package com.imalchemy.repository;

import com.imalchemy.model.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsBySlug(String slug);

    // The query uses `COALESCE` to return 0 if there are no categories yet, which prevents a null result.
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM Category c")
    Integer findMaxDisplayOrder();

    // The query uses `COALESCE` to return 0 if there are no files yet, which prevents a null result.
    @Query("SELECT COALESCE(MAX(c.totalFiles), 0) FROM Category c")
    Integer findMaxFiles();

    Optional<Category> findByNameIgnoreCase(String name);

}