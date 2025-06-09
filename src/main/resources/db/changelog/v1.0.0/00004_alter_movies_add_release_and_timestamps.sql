--liquibase formatted sql

--changeset author:00004_alter_movies_add_release_and_timestamps
ALTER TABLE movies
    ADD COLUMN release_date DATE,
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();

UPDATE movies
SET release_date = '2000-01-01'
WHERE release_date IS NULL;
--rollback ALTER TABLE movies DROP COLUMN IF EXISTS release_date, DROP COLUMN IF EXISTS updated_at;