ALTER TABLE image
    ADD is_light_mode BOOLEAN DEFAULT FALSE;

ALTER TABLE image
    ALTER COLUMN is_light_mode SET NOT NULL;