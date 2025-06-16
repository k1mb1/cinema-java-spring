--liquibase formatted sql

--changeset k1mb1:00003_create_watched_movies
CREATE TABLE watched_movies
(
    user_id    INT REFERENCES users (id) ON DELETE CASCADE,
    movie_id   INT REFERENCES movies (id) ON DELETE CASCADE,
    watched_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, movie_id)
);
--rollback DROP TABLE watched_movies;