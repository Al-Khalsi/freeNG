-- Create a standard index
CREATE INDEX idx_image_keywords ON image_keywords (keywords);

-- Create a GIN index for full-text search
CREATE INDEX idx_image_keywords_fts ON image_keywords USING gin(to_tsvector('english', keywords));