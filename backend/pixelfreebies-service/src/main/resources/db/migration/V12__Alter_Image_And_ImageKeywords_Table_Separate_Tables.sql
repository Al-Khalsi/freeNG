-- Create the new `keywords` table
CREATE TABLE keywords
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    keyword    VARCHAR(255)                            NOT NULL,
    image_id   UUID                                    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT pk_keywords PRIMARY KEY (id),
    CONSTRAINT fk_keywords_on_image FOREIGN KEY (image_id) REFERENCES images (id) ON DELETE CASCADE
);

-- Create a standard index
CREATE INDEX idx_keywords_on_image_id ON keywords (image_id);
CREATE INDEX idx_keywords_on_keyword ON keywords (keyword);

-- Create a GIN index for full-text search
CREATE INDEX idx_keywords_on_keyword_fts ON keywords USING gin (to_tsvector('english', keyword));