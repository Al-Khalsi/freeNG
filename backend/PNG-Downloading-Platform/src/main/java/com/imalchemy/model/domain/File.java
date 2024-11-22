package com.imalchemy.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    private String fileTitle;
    private String filePath;
    private String contentType;
    private long size;
    private int height;
    private int width;
    private boolean isActive;
    private String keywords; // Comma-separated keywords for search
    private String style;
    private boolean isLightMode = false;
    // Color palette for better search-ability
    @ElementCollection
    @CollectionTable(
            name = "file_colors",
            joinColumns = @JoinColumn(name = "file_id")
    )
    private Set<String> dominantColors = new HashSet<>();
    // Statistics and metrics
    private long viewCount;
    private long downloadCount;
    private BigDecimal averageRating;
    private LocalDateTime lastDownloadedAt;

    // -------------------- Relationships --------------------
    @ManyToOne
    private User uploadedBy;

    @ManyToMany
    @JoinTable(
            name = "files_categories",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

    // -------------------- Methods --------------------

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

}
