CREATE TABLE object
(
    id         CHAR(36) PRIMARY KEY NOT NULL,
    name       VARCHAR(128)         NOT NULL,
    image_url  VARCHAR(256),
    created_at TIMESTAMP            NOT NULL,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN              NOT NULL
);


