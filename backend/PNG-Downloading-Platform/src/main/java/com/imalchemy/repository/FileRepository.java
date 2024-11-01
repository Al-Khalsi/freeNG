package com.imalchemy.repository;

import com.imalchemy.model.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findByFileTitle(String fileTitle);
}