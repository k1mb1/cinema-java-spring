--liquibase formatted sql

--changeset author:00007_create_genres_and_movie_genres
CREATE TABLE genres
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE movie_genres
(
    movie_id   INT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    genre_id   INT NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (movie_id, genre_id)
);
--rollback DROP TABLE IF EXISTS movie_genres; DROP TABLE IF EXISTS genres;