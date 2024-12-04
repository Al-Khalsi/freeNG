-- Drop foreign key and column from keywords
ALTER TABLE keywords
    DROP CONSTRAINT fk_keywords_on_image;
ALTER TABLE keywords
    DROP COLUMN image_id;
DROP TABLE image_keywords;

-- Create the join table
CREATE TABLE images_keywords
(
    image_id   UUID   NOT NULL,
    keyword_id BIGINT NOT NULL,
    PRIMARY KEY (image_id, keyword_id),
    CONSTRAINT fk_image_keywords_on_image FOREIGN KEY (image_id) REFERENCES images (id) ON DELETE CASCADE,
    CONSTRAINT fk_image_keywords_on_keyword FOREIGN KEY (keyword_id) REFERENCES keywords (id) ON DELETE CASCADE
);
