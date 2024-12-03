package com.pixelfreebies.repository;

import com.pixelfreebies.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // TODO: configure this scenario [org.hibernate.NonUniqueResultException: Query did not return a unique result: 2 results were returned]
    Optional<User> findByEmail(String email);

}