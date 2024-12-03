package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RolesRepository extends JpaRepository<Roles, UUID> {

    Optional<Roles> findByRoleName(String roleName);

}