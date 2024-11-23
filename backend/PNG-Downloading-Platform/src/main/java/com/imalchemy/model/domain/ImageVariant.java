package com.imalchemy.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.imalchemy.model.enums.ImageFormat;
import com.imalchemy.model.enums.PURPOSE;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image_variants")
public class ImageVariant extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageFormat format;

    private String filePath;
    private String contentType;
    private long size;
    private int width;
    private int height;
    private PURPOSE purpose; // e.g., "download", "preview", "thumbnail"

    // -------------------- Relationships --------------------
    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

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