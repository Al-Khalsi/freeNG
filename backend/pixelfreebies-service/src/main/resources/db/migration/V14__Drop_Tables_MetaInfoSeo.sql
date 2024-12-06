-- Drop foreign key and column from keywords
ALTER TABLE meta_info_seo
    DROP CONSTRAINT FK_META_INFO_SEO_ON_IMAGE;

DROP TABLE meta_info_seo;