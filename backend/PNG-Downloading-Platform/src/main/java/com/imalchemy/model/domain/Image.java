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
@Table(name = "images")
public class Image extends BaseEntity<UUID> {

    @Id
    private UUID id;

    private String fileTitle;
    private boolean isActive;
    private String keywords; // Comma-separated keywords for search
    private String style;
    private boolean isLightMode;

    private int width;
    private int height;
    private String contentType;
    private long size;
    private String filePath;

    // Statistics and metrics
    private long viewCount;
    private long downloadCount;
    private BigDecimal averageRating;
    private LocalDateTime lastDownloadedAt;

    // Color palette for better search-ability
    @ElementCollection
    @CollectionTable(
            name = "image_colors",
            joinColumns = @JoinColumn(name = "image_id")
    )
    private Set<String> dominantColors = new HashSet<>();

    // -------------------- Relationships --------------------
    @ManyToOne
    private User uploadedBy;

    @ManyToMany
    @JoinTable(
            name = "images_categories",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ImageVariant> variants = new HashSet<>();

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
