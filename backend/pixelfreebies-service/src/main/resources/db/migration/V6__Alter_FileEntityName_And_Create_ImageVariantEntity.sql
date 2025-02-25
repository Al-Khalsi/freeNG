ALTER TABLE files_categories
    DROP CONSTRAINT fk_filcat_on_category;

ALTER TABLE files_categories
    DROP CONSTRAINT fk_filcat_on_file;

ALTER TABLE file_colors
    DROP CONSTRAINT fk_file_colors_on_file;

ALTER TABLE file
    DROP CONSTRAINT fk_file_on_uploadedby;

CREATE TABLE image_colors
(
    image_id        UUID NOT NULL,
    dominant_colors VARCHAR(255)
);

CREATE TABLE image_variants
(
    id                          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at                  TIMESTAMP WITHOUT TIME ZONE,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE,
    format                      VARCHAR(255),
    file_path                   VARCHAR(255),
    original_image_content_type VARCHAR(255),
    size                        BIGINT                                  NOT NULL,
    width                       INTEGER                                 NOT NULL,
    height                      INTEGER                                 NOT NULL,
    purpose                     SMALLINT,
    image_id                    UUID                                    NOT NULL,
    CONSTRAINT pk_image_variants PRIMARY KEY (id)
);

CREATE TABLE images
(
    id                 UUID    NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    file_title         VARCHAR(255),
    is_active          BOOLEAN NOT NULL,
    keywords           VARCHAR(255),
    style              VARCHAR(255),
    is_light_mode      BOOLEAN NOT NULL,
    width              INTEGER NOT NULL,
    height             INTEGER NOT NULL,
    content_type       VARCHAR(255),
    size               BIGINT  NOT NULL,
    file_path          VARCHAR(255),
    view_count         BIGINT  NOT NULL,
    download_count     BIGINT  NOT NULL,
    average_rating     DECIMAL,
    last_downloaded_at TIMESTAMP WITHOUT TIME ZONE,
    uploaded_by_id     UUID,
    CONSTRAINT pk_images PRIMARY KEY (id)
);

CREATE TABLE images_categories
(
    category_id BIGINT NOT NULL,
    image_id    UUID   NOT NULL,
    CONSTRAINT pk_images_categories PRIMARY KEY (category_id, image_id)
);

ALTER TABLE images
    ADD CONSTRAINT FK_IMAGES_ON_UPLOADEDBY FOREIGN KEY (uploaded_by_id) REFERENCES users (id);

ALTER TABLE image_variants
    ADD CONSTRAINT FK_IMAGE_VARIANTS_ON_IMAGE FOREIGN KEY (image_id) REFERENCES images (id);

ALTER TABLE images_categories
    ADD CONSTRAINT fk_imacat_on_category FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE images_categories
    ADD CONSTRAINT fk_imacat_on_image FOREIGN KEY (image_id) REFERENCES images (id);

ALTER TABLE image_colors
    ADD CONSTRAINT fk_image_colors_on_image FOREIGN KEY (image_id) REFERENCES images (id);

DROP TABLE file CASCADE;

DROP TABLE file_colors CASCADE;

DROP TABLE files_categories CASCADE;