package com.pixelfreebies.model.domain;

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
// TODO: show filters based on most searched keywords
public class Image extends BaseEntity<UUID> {

    @Id
    private UUID id;

    private String fileTitle;
    private boolean isActive;
    private String style;
    private boolean isLightMode;
    private String source;

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
    @Builder.Default
    private Set<String> dominantColors = new HashSet<>();

    // -------------------- Relationships --------------------
    @ManyToOne
    private User uploadedBy;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<ImageVariant> variants = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "images_keywords",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Keywords> keywords = new HashSet<>();

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
