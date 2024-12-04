package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    @Query(value = """
            SELECT i.*
            FROM images i
            LEFT JOIN images_keywords ik ON i.id = ik.image_id
            LEFT JOIN keywords k ON ik.keyword_id = k.id
            WHERE
            (:query IS NULL OR
            to_tsvector('english', i.file_title) @@ to_tsquery('english', :query) OR
            to_tsvector('english', k.keyword) @@ to_tsquery('english', :query))
            """, nativeQuery = true)
    List<Image> searchFiles(@Param("query") String query);

    @Query(value = """
            SELECT i.*
            FROM images i
            LEFT JOIN images_keywords ik ON i.id = ik.image_id
            LEFT JOIN keywords k ON ik.keyword_id = k.id
            WHERE
            i.file_title ILIKE CONCAT('%', :query, '%') OR
            k.keyword ILIKE CONCAT('%', :query, '%')
            """, nativeQuery = true)
    List<Image> searchSimilarFiles(@Param("query") String query);

    @Query(value = """
            SELECT k.keyword
            FROM keywords k
            LEFT JOIN images_keywords ik ON k.id = ik.keyword_id
            WHERE
            (:query IS NULL OR
            to_tsvector('english', k.keyword) @@ to_tsquery('english', :query))
            """, nativeQuery = true)
    List<String> searchKeywords(String query);

    @Query(value = """
            SELECT k.keyword
            FROM keywords k
            LEFT JOIN images_keywords ik ON k.id = ik.keyword_id
            WHERE
            k.keyword ILIKE CONCAT('%', :query, '%')
            """, nativeQuery = true)
    List<String> searchSimilarKeywords(String query);

    @Query(value = """
            SELECT i.*
            FROM images i
            LEFT JOIN images_keywords ik ON i.id = ik.image_id
            LEFT JOIN keywords k ON ik.keyword_id = k.id
            WHERE
            (:query IS NULL OR
            to_tsvector('english', i.file_title) @@ to_tsquery('english', :query) OR
            to_tsvector('english', k.keyword) @@ to_tsquery('english', :query))
            """,
            countQuery = """
                    SELECT COUNT(i.*)
                    FROM images i
                    LEFT JOIN images_keywords ik ON i.id = ik.image_id
                    LEFT JOIN keywords k ON ik.keyword_id = k.id
                    WHERE
                    (:query IS NULL OR
                    to_tsvector('english', i.file_title) @@ to_tsquery('english', :query) OR
                    to_tsvector('english', k.keyword) @@ to_tsquery('english', :query))
                    """,
            nativeQuery = true)
    Page<Image> searchFiles(@Param("query") String query, Pageable pageable);

    @Query(value = """
            SELECT i.*
            FROM images i
            LEFT JOIN images_keywords ik ON i.id = ik.image_id
            LEFT JOIN keywords k ON ik.keyword_id = k.id
            WHERE
            i.file_title ILIKE CONCAT('%', :query, '%') OR
            k.keyword ILIKE CONCAT('%', :query, '%')
            """,
            countQuery = """
                    SELECT COUNT(i.*)
                    FROM images i
                    LEFT JOIN images_keywords ik ON i.id = ik.image_id
                    LEFT JOIN keywords k ON ik.keyword_id = k.id
                    WHERE
                    i.file_title ILIKE CONCAT('%', :query, '%') OR
                    k.keyword ILIKE CONCAT('%', :query, '%')
                    """,
            nativeQuery = true)
    Page<Image> searchSimilarFiles(@Param("query") String query, Pageable pageable);

}