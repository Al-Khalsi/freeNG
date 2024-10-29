-- Create files table to store file details
CREATE TABLE file
(
    id             UUID   NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    file_title     VARCHAR(255),
    file_path      VARCHAR(255),
    content_type   VARCHAR(255),
    size           BIGINT NOT NULL,
    height         INTEGER,
    width          INTEGER,
    download_count BIGINT        DEFAULT 0,
    is_active      BOOLEAN       DEFAULT TRUE,
    average_rating DECIMAL(5, 2) DEFAULT 0.00,

    -- Foreign key reference to users (uploadedBy)
    uploaded_by_id UUID,

    CONSTRAINT pk_file PRIMARY KEY (id)
);

-- Foreign key constraint to reference the user who uploaded the file
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_UPLOADEDBY FOREIGN KEY (uploaded_by_id) REFERENCES users (id);
-- TODO: add indexes!!!