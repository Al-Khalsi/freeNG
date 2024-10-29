-- Users Table
CREATE TABLE users
(
    id             BIGSERIAL PRIMARY KEY,
    username       VARCHAR(50) UNIQUE  NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL,
    password_hash  VARCHAR(255)        NOT NULL,
    role           user_role DEFAULT 'USER', -- Use a defined enum type
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login     TIMESTAMP DEFAULT NULL,
    is_active      BOOLEAN   DEFAULT true,   -- Added for user status tracking
    login_attempts INT       DEFAULT 0       -- Added for security
);

-- Define ENUM for user roles
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

-- Comments on users columns
COMMENT ON COLUMN users.username IS 'Username for login';
COMMENT ON COLUMN users.email IS 'Email for notifications and recovery';
COMMENT ON COLUMN users.password_hash IS 'Hashed password';
COMMENT ON COLUMN users.role IS 'USER or ADMIN';
COMMENT ON COLUMN users.created_at IS 'Account creation timestamp';
COMMENT ON COLUMN users.last_login IS 'Last successful login';
COMMENT ON COLUMN users.is_active IS 'Account status';
COMMENT ON COLUMN users.login_attempts IS 'Failed login tracking';

-- Index for user authentication queries
CREATE INDEX idx_users_email_password ON users (email, password_hash);
-- Index for user activity monitoring
CREATE INDEX idx_users_last_login ON users (last_login) WHERE last_login IS NOT NULL;

-- Categories Table
CREATE TABLE categories
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(50) UNIQUE NOT NULL,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active     BOOLEAN   DEFAULT true, -- Added for soft delete
    display_order INT       DEFAULT 0     -- Added for custom ordering
);

-- Comments on categories columns
COMMENT ON COLUMN categories.name IS 'Category name';
COMMENT ON COLUMN categories.description IS 'Category description';
COMMENT ON COLUMN categories.is_active IS 'Soft delete flag';
COMMENT ON COLUMN categories.display_order IS 'Custom ordering';

-- Index for category listing and searching
CREATE INDEX idx_categories_active_order ON categories (is_active, display_order);

-- Images Table
CREATE TABLE images
(
    id                 BIGSERIAL PRIMARY KEY,
    title              VARCHAR(100) NOT NULL,
    description        TEXT,
    file_path          VARCHAR(255) NOT NULL,
    file_size          BIGINT       NOT NULL,
    width              INT          NOT NULL,
    height             INT          NOT NULL,
    format             VARCHAR(10)  NOT NULL,
    download_count     BIGINT        DEFAULT 0,
    category_id        BIGINT,
    uploaded_by        BIGINT       NOT NULL,
    created_at         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    is_active          BOOLEAN       DEFAULT true, -- Added for soft delete
    last_downloaded_at TIMESTAMP     DEFAULT NULL,-- Added for analytics
    average_rating     DECIMAL(3, 2) DEFAULT 0.00,-- Added for image rating
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (uploaded_by) REFERENCES users (id)
);

-- Comments on images columns
COMMENT ON COLUMN images.title IS 'Image title';
COMMENT ON COLUMN images.description IS 'Image description';
COMMENT ON COLUMN images.file_path IS 'Path in storage';
COMMENT ON COLUMN images.file_size IS 'Size in bytes';
COMMENT ON COLUMN images.format IS 'Image format (PNG)';
COMMENT ON COLUMN images.download_count IS 'Total downloads';
COMMENT ON COLUMN images.is_active IS 'Soft delete flag';

-- Composite index for search by title and category
CREATE INDEX idx_images_title_category ON images (category_id, title);
-- Index for popular images (high download count)
CREATE INDEX idx_images_downloads ON images (download_count DESC) WHERE is_active = true;
-- Index for recent images
CREATE INDEX idx_images_recent ON images (created_at DESC) WHERE is_active = true;
-- Composite index for user uploads
CREATE INDEX idx_images_user_date ON images (uploaded_by, created_at DESC);
-- Index for format filtering
CREATE INDEX idx_images_format ON images (format) WHERE is_active = true;

-- Tags Table
CREATE TABLE tags
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(50) UNIQUE NOT NULL,
    use_count INT DEFAULT 0 -- Added for tag popularity tracking
);

-- Comments on tags columns
COMMENT ON COLUMN tags.name IS 'Tag name';
COMMENT ON COLUMN tags.use_count IS 'Number of images using this tag';

-- Index for tag search
CREATE INDEX idx_tags_popularity ON tags (use_count DESC);

-- Image Tags (Junction Table)
CREATE TABLE image_tags
(
    image_id BIGINT,
    tag_id   BIGINT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Added for tracking
    PRIMARY KEY (image_id, tag_id),
    FOREIGN KEY (image_id) REFERENCES images (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

-- Index for tag-based image search
CREATE INDEX idx_image_tags_tag ON image_tags (tag_id);

-- Download History Table
CREATE TABLE download_history
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT,
    image_id        BIGINT NOT NULL,
    downloaded_at   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    ip_address      VARCHAR(45),
    ad_shown        BOOLEAN         DEFAULT false,
    download_status download_status DEFAULT 'STARTED', -- Use a defined enum type
    completion_time TIMESTAMP       DEFAULT NULL,      -- Added for performance monitoring
    user_agent      VARCHAR(255),                      -- Added for analytics
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (image_id) REFERENCES images (id)
);

-- Define ENUM for download statuses
CREATE TYPE download_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

-- Comments on download_history columns
COMMENT ON COLUMN download_history.download_status IS 'STARTED, COMPLETED, FAILED';

-- Index for user download history
CREATE INDEX idx_downloads_user ON download_history (user_id, downloaded_at DESC);
-- Index for image download history
CREATE INDEX idx_downloads_image ON download_history (image_id, downloaded_at DESC);
-- Index for IP-based rate limiting
CREATE INDEX idx_downloads_ip_recent ON download_history (ip_address, downloaded_at DESC);
-- Partial index for failed downloads
CREATE INDEX idx_downloads_failed ON download_history (downloaded_at)
    WHERE download_status = 'FAILED';

-- Admin Activity Log
CREATE TABLE admin_activity_log
(
    id            BIGSERIAL PRIMARY KEY,
    admin_id      BIGINT        NOT NULL,
    activity_type activity_type NOT NULL,
    entity_id     BIGINT        NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details       JSON      DEFAULT NULL, -- Added for storing additional activity details
    ip_address    VARCHAR(45),            -- Added for security tracking
    FOREIGN KEY (admin_id) REFERENCES users (id)
);

-- Define ENUM for activity types
CREATE TYPE activity_type AS ENUM ('IMAGE_UPLOAD', 'IMAGE_DELETE', 'CATEGORY_CREATE', 'CATEGORY_UPDATE');

-- Comments on admin_activity_log columns
COMMENT ON COLUMN admin_activity_log.activity_type IS 'IMAGE_UPLOAD, IMAGE_DELETE, CATEGORY_CREATE, CATEGORY_UPDATE';
COMMENT ON COLUMN admin_activity_log.details IS 'Additional activity details';

-- Index for admin activity monitoring
CREATE INDEX idx_admin_activity_user ON admin_activity_log (admin_id, created_at DESC);
-- Index for entity-specific activity lookup
CREATE INDEX idx_admin_activity_entity ON admin_activity_log (entity_id, activity_type);