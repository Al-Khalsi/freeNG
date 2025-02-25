package com.pixelfreebies.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseEntity is an abstract class that provides common properties and methods
 * for all entities in the application. It uses JPA annotations to handle
 * auditing of creation and modification timestamps.
 *
 * @param <T> the type of the identifier for the entity
 */
@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<T> {

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Get the identifier of the entity. This method must be implemented by
     * subclasses to return the specific type of the entity's identifier.
     *
     * @return the identifier of the entity
     */
    public abstract T getId();

}
