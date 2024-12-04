ALTER TABLE image_categories
DROP
CONSTRAINT fk_imacat_on_category;

ALTER TABLE image_categories
DROP
CONSTRAINT fk_imacat_on_image;

ALTER TABLE image_subcategories
DROP
CONSTRAINT fk_imasub_on_image;

ALTER TABLE image_subcategories
DROP
CONSTRAINT fk_imasub_on_sub_category;

ALTER TABLE sub_categories
DROP
CONSTRAINT fk_sub_categories_on_parent_category;

CREATE TABLE image_keywords
(
    image_id UUID NOT NULL,
    keywords VARCHAR(255)
);

ALTER TABLE image_keywords
    ADD CONSTRAINT fk_image_keywords_on_image FOREIGN KEY (image_id) REFERENCES images (id);

DROP TABLE category CASCADE;

DROP TABLE image_categories CASCADE;

DROP TABLE image_subcategories CASCADE;

DROP TABLE sub_categories CASCADE;

ALTER TABLE images
DROP
COLUMN keywords;