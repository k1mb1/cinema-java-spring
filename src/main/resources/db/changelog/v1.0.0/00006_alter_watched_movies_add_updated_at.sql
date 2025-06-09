--liquibase formatted sql

--changeset author:00006_alter_watched_movies_add_updated_at
ALTER TABLE watched_movies
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
--rollback ALTER TABLE watched_movies DROP COLUMN IF EXISTS updated_at;