CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    role VARCHAR(32) NOT NULL,
    vendor_id BIGINT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT app_user_role_check CHECK (role IN ('PLATFORM_ADMIN', 'VENDOR_ADMIN', 'VENDOR_STAFF')),
    CONSTRAINT app_user_vendor_context_check CHECK (
        (role = 'PLATFORM_ADMIN' AND vendor_id IS NULL)
        OR (role IN ('VENDOR_ADMIN', 'VENDOR_STAFF') AND vendor_id IS NOT NULL)
    )
);

CREATE INDEX idx_app_user_vendor_id ON app_user(vendor_id);
