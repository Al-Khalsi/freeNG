package com.pixelfreebies.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String slug; // URL-friendly version of the name
    private String iconUrl; // For category icon/thumbnail
    private boolean isActive;
    private int displayOrder = 0; // For controlling the display sequence
    private int totalFiles; // Cache for file count
    private int level; // Hierarchy level
    private boolean isParent;

    // -------------------- Relationships --------------------
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<SubCategory> subCategories = new HashSet<>();

    // Bidirectional many-to-many relationship with File
    @ManyToMany(mappedBy = "categories")
    private Set<Image> images = new HashSet<>();

    // -------------------- Methods --------------------

    /**
     * Overrides the default method to provide a clearer name.
     *
     * @return the UUID of the user
     */
    @Override
    @JsonProperty("id")
    public Long getId() {
        return this.id;
    }

}
