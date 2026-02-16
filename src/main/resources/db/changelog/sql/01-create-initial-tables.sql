--liquibase formatted sql

--changeset salem:1
CREATE TABLE users (
    -- Internal surrogate primary key for efficient linking and performance
    id BIGSERIAL PRIMARY KEY,
    
    -- External reference ID from Keycloak (Subject ID). Must be unique to identify the user.
    keycloak_id VARCHAR(50) NOT NULL UNIQUE,
    
    -- Mirror data for display purposes (Syncs with Keycloak token).
    -- No Unique constraint on email here; Keycloak is the Single Source of Truth.
    email VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index to optimize user lookup during authentication flow (extracting user from token)
CREATE INDEX idx_users_keycloak_id ON users(keycloak_id);


CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    
    -- Stores the S3/MinIO object key (path) for the uploaded image, not the image itself.
    image_key VARCHAR(255),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    -- Linking to the internal numeric User ID for better join performance.
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Index to speed up retrieving all notes for a specific user
CREATE INDEX idx_notes_user_id ON notes(user_id);