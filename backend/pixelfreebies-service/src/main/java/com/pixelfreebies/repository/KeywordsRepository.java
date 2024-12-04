package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.Keywords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordsRepository extends JpaRepository<Keywords, Long> {
    Optional<Keywords> findByKeyword(String keyword);
}