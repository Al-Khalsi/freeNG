-- Images
-- Indexes for frequently used search and filter conditions
CREATE INDEX idx_image_is_active ON images (is_active);
CREATE INDEX idx_image_uploaded_by ON images (uploaded_by_id);
CREATE INDEX idx_image_keywords ON images (keywords); -- For full-text search
CREATE INDEX idx_image_style ON images (style);
CREATE INDEX idx_image_view_count ON images (view_count);
CREATE INDEX idx_image_download_count ON images (download_count);
CREATE INDEX idx_image_average_rating ON images (average_rating);
CREATE INDEX idx_image_last_downloaded ON images (last_downloaded_at);
-- Composite indexes for complex queries
CREATE INDEX idx_image_search ON images (file_title, keywords);
CREATE INDEX idx_image_search_more_fields ON images (is_active, style, view_count);

-- Category
-- Indexes for category-related queries
CREATE INDEX idx_category_is_active ON category (is_active);
CREATE INDEX idx_category_display_order ON category (display_order);
CREATE INDEX idx_category_name ON category (name);
CREATE INDEX idx_category_slug ON category (slug);

-- SubCategory
-- Indexes for subcategory-related queries
CREATE INDEX idx_subcategory_is_active ON sub_categories (is_active);
CREATE INDEX idx_subcategory_parent_category ON sub_categories (parent_category_id);
CREATE INDEX idx_subcategory_display_order ON sub_categories (display_order);
CREATE INDEX idx_subcategory_name ON sub_categories (name);
CREATE INDEX idx_subcategory_slug ON sub_categories (slug);

-- ImageVariant
-- Indexes for image variant queries
CREATE INDEX idx_image_variant_image_id ON image_variants (image_id);
CREATE INDEX idx_image_variant_format ON image_variants (format);
CREATE INDEX idx_image_variant_purpose ON image_variants (purpose);

select i.* from images i where i.file_title ilike '%a%';