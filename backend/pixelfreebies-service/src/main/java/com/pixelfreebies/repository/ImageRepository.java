package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    @Query(value = """
            SELECT i.*
            FROM images i
            LEFT JOIN image_keywords ik ON i.id = ik.image_id
            WHERE
            (:query IS NULL OR
            to_tsvector('english', i.file_title) @@ to_tsquery('english', :query) OR
            to_tsvector('english', ik.keywords) @@ to_tsquery('english', :query))
            """, nativeQuery = true)
    List<Image> searchFiles(@Param("query") String query);

    @Query(value = """
            SELECT i.*
            FROM images i
            LEFT JOIN image_keywords ik ON i.id = ik.image_id
            WHERE
            i.file_title ILIKE CONCAT('%', :query, '%') OR
            ik.keywords ILIKE CONCAT('%', :query, '%')
            """, nativeQuery = true)
    List<Image> searchSimilarFiles(@Param("query") String query);

    @Query(value = """
            SELECT ik.keywords
            FROM image_keywords ik
            WHERE
            (:query IS NULL OR
            to_tsvector('english', ik.keywords) @@ to_tsquery('english', :query))
            """, nativeQuery = true)
    List<String> searchKeywords(String query);

    @Query(value = """
            SELECT ik.keywords
            FROM image_keywords ik
            WHERE
            ik.keywords ILIKE CONCAT('%', :query, '%')
            """, nativeQuery = true)
    List<String> searchSimilarKeywords(String query);

}