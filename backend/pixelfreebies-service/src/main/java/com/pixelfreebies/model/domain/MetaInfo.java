package com.pixelfreebies.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meta_info_seo")
public class MetaInfo extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metaTitle;
    private String nameLink;
    private String description;

    @OneToOne
    private Image image;

}
