ALTER TABLE image
    ADD style VARCHAR(255);

ALTER TABLE image
    ALTER COLUMN view_count SET NOT NULL;