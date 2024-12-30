-- Step 1: Rename the style column
ALTER TABLE images
    RENAME COLUMN style TO styles;

-- Step 2: Create the new image_styles table
CREATE TABLE image_styles
(
    image_id UUID         NOT NULL,
    styles   VARCHAR(255) NOT NULL,
    PRIMARY KEY (image_id, styles),
    FOREIGN KEY (image_id) REFERENCES images (id) ON DELETE CASCADE
);

-- Step 3: Insert existing styles into the new table
INSERT INTO image_styles (image_id, styles)
SELECT id, styles
FROM images
WHERE styles IS NOT NULL;

-- Step 4: Drop the old style column
ALTER TABLE images
    DROP COLUMN styles;