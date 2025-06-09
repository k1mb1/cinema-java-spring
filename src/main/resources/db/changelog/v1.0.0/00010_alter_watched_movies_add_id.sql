--liquibase formatted sql

--changeset author:00010_alter_watched_movies_add_id
-- First drop the primary key constraint
ALTER TABLE watched_movies DROP CONSTRAINT watched_movies_pkey;

-- Add an ID column
ALTER TABLE watched_movies
    ADD COLUMN id SERIAL PRIMARY KEY,
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();

-- Keep the unique constraint on user_id and movie_id to prevent duplicates
ALTER TABLE watched_movies
    ADD CONSTRAINT watched_movies_user_id_movie_id_unique UNIQUE (user_id, movie_id);
--rollback ALTER TABLE watched_movies DROP COLUMN IF EXISTS id; DROP COLUMN IF EXISTS created_at; ALTER TABLE watched_movies ADD PRIMARY KEY (user_id, movie_id);
