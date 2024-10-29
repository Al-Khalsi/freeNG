package com.imalchemy.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class File extends BaseEntity<UUID> {

    @Id
    private UUID id;

    /**
     * Overrides the default method to provide a clearer name.
     *
     * @return the UUID of the user
     */
    @Override
    @JsonProperty("id")
    public UUID getId() {
        return this.id;
    }

    /**
     * Method to handle operations before persisting a new User entity.
     */
    @PrePersist
    public void onCreate() {
        // Generate a new UUID if the id is not already set
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    private String fileTitle;
    private String filePath;
    private String contentType;
    private long size;
    private int height;
    private int width;
    private long downloadCount;
    private boolean isActive;
    private BigDecimal averageRating;

    // -------------------- Relationships --------------------
    @ManyToOne
    private User uploadedBy;

}
