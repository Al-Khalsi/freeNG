package com.pixelfreebies.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pixelfreebies.model.enums.ImageFormat;
import com.pixelfreebies.model.enums.Purpose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "image_variants")
public class ImageVariant extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageFormat format;

    private String filePath;
    private String originalImageContentType;
    private long size;
    private int width;
    private int height;
    private Purpose purpose; // e.g., "download", "preview", "thumbnail"

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