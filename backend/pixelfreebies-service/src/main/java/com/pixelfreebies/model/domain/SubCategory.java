package com.pixelfreebies.model.domain;

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
@Table(name = "sub_categories")
public class SubCategory extends BaseEntity<Long> {

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

    // Reference to the parent category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", nullable = false)
    private Category parentCategory;

    // Bidirectional many-to-many relationship with Image
    @ManyToMany(mappedBy = "subCategories")
    private Set<Image> images = new HashSet<>();

    // -------------------- Methods --------------------
    @Override
    @JsonProperty("id")
    public Long getId() {
        return this.id;
    }

}
