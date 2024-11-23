package com.imalchemy.repository;

import com.imalchemy.model.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<Image, UUID> {

    Optional<Image> findByFileTitle(String fileTitle);

    @Query(value = """
            SELECT f.*
            FROM file f WHERE
            (:query IS NULL OR
            to_tsvector('english', f.file_title) @@ to_tsquery('english', :query) OR
            to_tsvector('english', f.keywords) @@ to_tsquery('english', :query))
            """, nativeQuery = true)
    List<Image> searchFiles(@Param("query") String query);

    @Query(value = """
            SELECT f.*
            FROM file f WHERE
            f.file_title ILIKE CONCAT('%', :query, '%') OR
            f.keywords ILIKE CONCAT('%', :query, '%')
            """, nativeQuery = true)
    List<Image> searchSimilarFiles(@Param("query") String query);

}