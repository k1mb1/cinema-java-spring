--liquibase formatted sql

--changeset author:00001_create_users
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
--rollback DROP TABLE users;