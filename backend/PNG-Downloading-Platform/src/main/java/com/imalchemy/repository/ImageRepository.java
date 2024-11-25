package com.imalchemy.repository;

import com.imalchemy.model.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    Optional<Image> findByFileTitle(String fileTitle);

    @Query(value = """
            SELECT i.*
            FROM images i WHERE
            (:query IS NULL OR
            to_tsvector('english', i.file_title) @@ to_tsquery('english', :query) OR
            to_tsvector('english', i.keywords) @@ to_tsquery('english', :query))
            """, nativeQuery = true)
    List<Image> searchFiles(@Param("query") String query);

    @Query(value = """
            SELECT i.*
            FROM images i WHERE
            i.file_title ILIKE CONCAT('%', :query, '%') OR
            i.keywords ILIKE CONCAT('%', :query, '%')
            """, nativeQuery = true)
    List<Image> searchSimilarFiles(@Param("query") String query);

}