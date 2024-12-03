package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.ImageVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageVariantRepository extends JpaRepository<ImageVariant, Long> {
}