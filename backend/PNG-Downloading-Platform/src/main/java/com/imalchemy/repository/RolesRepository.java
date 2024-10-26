package com.imalchemy.repository;

import com.imalchemy.model.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolesRepository extends JpaRepository<Roles, UUID> {
}