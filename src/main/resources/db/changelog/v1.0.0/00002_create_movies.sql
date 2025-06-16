--liquibase formatted sql

--changeset k1mb1:00002_create_movies
CREATE TABLE movies
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    year        INT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
--rollback DROP TABLE movies;