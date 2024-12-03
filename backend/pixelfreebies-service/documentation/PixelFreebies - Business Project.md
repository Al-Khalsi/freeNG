# PNG Image Download Platform - Enhanced Technical Specification

[TOC]

## Core Idea

A free PNG image downloading platform with ad-based monetization. Downloads are triggered after ad display and processed
asynchronously in the background.

## Technical Requirements

### 1. Authentication & Authorization

* User login/signup functionality
* Role-based access control (User/Admin)
* Token-based authentication

### 2. Image Management

* Browse, search, and filter images
* Category and tag-based classification
* Related images recommendation system
* Query optimization through proper indexing:
    - Title-based search indexing
    - Category-based filtering indexes
    - Tag-based search indexes
    - Composite indexes for complex queries

### 3. Download System

* Asynchronous download processing using Spring WebFlux
* Streaming large files efficiently
* Background processing of download requests
* Download history tracking
* Rate limiting implementation:
    - Per-user download limits
    - IP-based rate limiting
    - Configurable throttling parameters
    - Different rate limits for authenticated vs anonymous users

### 4. Admin Features

* Image upload and management
* Activity tracking and analytics
* Batch operations support
* Performance monitoring

### 5. Database Design

* Optimized schema with proper relationships
* Implemented indexes for frequent queries
* Audit trails for important operations
* Cache implementation for frequently accessed data

### 6. Performance Considerations

* Image caching strategy
* Query optimization through indexes
* Rate limiting for API endpoints
* Async processing for heavy operations

### 7. Security

* Input validation
* XSS protection
* CSRF protection
* Rate limiting for security
* File type validation

### 8. Monitoring

* Download analytics
* User activity tracking
* Performance metrics
* Error logging

## Database Schema Requirements

* Users table with role management
* Images table with metadata
* Categories and tags management
* Download history tracking
* Admin activity logging
* Proper indexing strategy

### Generated Demo Schemas

```postgresql
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
```

### Generated ID Types

````markdown
Let me analyze the pros and cons of different ID strategies for this PNG image download platform.

For most tables in this system, I recommend using UUIDs (specifically UUID v4) over sequence numbers, but let's break down the reasoning for each main table:

1. Users Table:
- Recommendation: UUID
- Rationale:
  - Security: Prevents user enumeration attacks (can't guess other user IDs)
  - Scalability: No coordination needed across multiple instances
  - Privacy: Makes it harder to determine total user count or user join order
  - Distribution-friendly: If you need to shard the database later

2. Images Table:
- Recommendation: UUID
- Rationale:
  - URLs become unpredictable (prevents scraping)
  - Easier CDN distribution
  - No collision concerns when bulk importing images
  - Better for potential multi-region deployment

3. Download History:
- Recommendation: UUID
- Rationale:
  - Consistent with parent tables (users and images)
  - Each download event is unique
  - Helps with distributed systems scenarios

4. Categories Table:
- Recommendation: Serial/Sequence numbers
- Rationale:
  - Small, admin-managed table
  - Categories are often referenced by number in URLs
  - Easy to maintain and reference
  - Categories are public information, no security concern

5. Tags Table:
- Recommendation: Serial/Sequence numbers
- Rationale:
  - Similar to categories
  - Limited set maintained by admins
  - Frequently joined in queries
  - No security implications

Here's a sample schema showing the ID strategy:

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    -- other fields
);

-- Images table
CREATE TABLE images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    -- other fields
);

-- Categories table
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    -- other fields
);

-- Tags table
CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    -- other fields
);

-- Download history table
CREATE TABLE download_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    image_id UUID REFERENCES images(id),
    -- other fields
);
```

Reasons NOT to use sequence numbers for main tables (Users, Images, Downloads):
1. Security concerns with predictable IDs
2. Potential race conditions in high-write scenarios
3. Complications with data sharding
4. Makes it easy to determine total counts and growth
5. Possible collisions during bulk operations

Reasons NOT to use UUIDs for lookup tables (Categories, Tags):
1. Takes more space unnecessarily
2. More complex for manual reference
3. Harder to work with in admin interfaces
4. No security benefit for public data
5. More complex indexes

Additional Considerations:
1. Use UUID version 4 for randomness
2. Consider using KSUID or ULID if timestamp ordering is important
3. Index the UUID columns appropriately
4. Use UUID generator on the application side for better control
5. Consider using shorter IDs (e.g., Snowflake) if URL length is a concern

Remember to:
- Create appropriate indexes for foreign key columns
- Use constraint names for easier maintenance
- Consider partial indexes for common queries
- Use appropriate UUID functions in your database

Would you like me to elaborate on any of these points or provide more specific implementation details for any particular aspect?
````

### Generated DBML Code

```sql
// Users table
Table users {
  id bigint [pk, increment]
  username varchar(50) [unique, not null, note: 'Username for login']
  email varchar(100) [unique, not null, note: 'Email for notifications and recovery']
  password_hash varchar(255) [not null, note: 'Hashed password']
  role enum [note: 'USER or ADMIN', default: 'USER']
  created_at timestamp [default: `CURRENT_TIMESTAMP`, note: 'Account creation timestamp']
  last_login timestamp [default: null, note: 'Last successful login']
  is_active boolean [default: true, note: 'Account status']
  login_attempts int [default: 0, note: 'Failed login tracking']

  indexes {
    (email, password_hash) [name: 'idx_users_email_password', note: 'For login queries']
    last_login [name: 'idx_users_last_login', note: 'For activity monitoring']
  }
}

// Categories table
Table categories {
  id bigint [pk, increment]
  name varchar(50) [unique, not null, note: 'Category name']
  description text [note: 'Category description']
  created_at timestamp [default: `CURRENT_TIMESTAMP`]
  is_active boolean [default: true, note: 'Soft delete flag']
  display_order int [default: 0, note: 'Custom ordering']

  indexes {
    (is_active, display_order) [name: 'idx_categories_active_order']
  }
}

// Images table
Table images {
  id bigint [pk, increment]
  title varchar(100) [not null, note: 'Image title']
  description text [note: 'Image description']
  file_path varchar(255) [not null, note: 'Path in storage']
  file_size bigint [not null, note: 'Size in bytes']
  width int [not null]
  height int [not null]
  format varchar(10) [not null, note: 'Image format (PNG)']
  download_count bigint [default: 0, note: 'Total downloads']
  category_id bigint [ref: > categories.id]
  uploaded_by bigint [ref: > users.id, not null]
  created_at timestamp [default: `CURRENT_TIMESTAMP`]
  is_active boolean [default: true, note: 'Soft delete flag']
  last_downloaded_at timestamp [default: null]
  average_rating decimal(3,2) [default: 0.00]

  indexes {
    (category_id, title) [name: 'idx_images_title_category']
    download_count [name: 'idx_images_downloads']
    created_at [name: 'idx_images_recent']
    (uploaded_by, created_at) [name: 'idx_images_user_date']
    format [name: 'idx_images_format']
  }
}

// Tags table
Table tags {
  id bigint [pk, increment]
  name varchar(50) [unique, not null, note: 'Tag name']
  use_count int [default: 0, note: 'Number of images using this tag']

  indexes {
    use_count [name: 'idx_tags_popularity']
  }
}

// Image Tags junction table
Table image_tags {
  image_id bigint [ref: > images.id]
  tag_id bigint [ref: > tags.id]
  added_at timestamp [default: `CURRENT_TIMESTAMP`]

  indexes {
    (image_id, tag_id) [pk]
    tag_id [name: 'idx_image_tags_tag']
  }
}

// Download History table
Table download_history {
  id bigint [pk, increment]
  user_id bigint [ref: > users.id]
  image_id bigint [ref: > images.id, not null]
  downloaded_at timestamp [default: `CURRENT_TIMESTAMP`]
  ip_address varchar(45)
  ad_shown boolean [default: false]
  download_status enum [note: 'STARTED, COMPLETED, FAILED', default: 'STARTED']
  completion_time timestamp [default: null]
  user_agent varchar(255)

  indexes {
    (user_id, downloaded_at) [name: 'idx_downloads_user']
    (image_id, downloaded_at) [name: 'idx_downloads_image']
    (ip_address, downloaded_at) [name: 'idx_downloads_ip_recent']
    downloaded_at [name: 'idx_downloads_failed', note: 'Partial index for failed downloads']
  }
}

// Admin Activity Log table
Table admin_activity_log {
  id bigint [pk, increment]
  admin_id bigint [ref: > users.id, not null]
  activity_type enum [note: 'IMAGE_UPLOAD, IMAGE_DELETE, CATEGORY_CREATE, CATEGORY_UPDATE']
  entity_id bigint [not null]
  created_at timestamp [default: `CURRENT_TIMESTAMP`]
  details json [default: null, note: 'Additional activity details']
  ip_address varchar(45)

  indexes {
    (admin_id, created_at) [name: 'idx_admin_activity_user']
    (entity_id, activity_type) [name: 'idx_admin_activity_entity']
  }
}
```

### Generated Relationship Diagram

![relationship_diagram](D:\Skill-Development\Spring-boot-Tutorial\source-code\Resume-Projects\ImAlchemy\freeNG\backend\PNG-Downloading-Platform\data\db\imalchemy.png)

## API Endpoints Required

* Authentication endpoints
* Image management endpoints
* Download processing endpoints
* Admin management endpoints
* Analytics endpoints

## Integration Points

* Cloud storage for images
* Ad service integration
* Analytics integration
* Monitoring integration

## Frontend Delegation

The frontend team will handle:

* User interface implementation
* Ad display integration
* Download progress indication
* Search interface
* Admin dashboard UI
* Client-side caching

## Technical Stack

* Backend: Spring Boot with WebFlux
* Database: PostgreSQL
* Cache: Redis/Caffeine
* Storage: Cloud storage (AWS S3 or similar)
* Security: Spring Security
* Documentation: OpenAPI/Swagger

## Performance Requirements

* Download processing: Asynchronous
* Search queries: < 500ms response time
* Image metadata retrieval: < 200ms
* Rate limiting: Configurable per endpoint
* Caching: Implementation required for frequent queries

## Monitoring Requirements

* Download success/failure rates
* API endpoint performance
* Error rates and types
* User activity patterns
* Resource utilization

## Development Notes

* Start with monolithic architecture
* Focus on core functionality first
* Implement proper error handling
* Follow REST best practices
* Implement comprehensive logging
* Use proper versioning