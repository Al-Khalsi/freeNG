package com.imalchemy.repository;

import com.imalchemy.model.domain.ImageVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageVariantRepository extends JpaRepository<ImageVariant, Long> {
}