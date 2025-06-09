--liquibase formatted sql

--changeset author:00009_create_countries_and_movie_countries
CREATE TABLE countries
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE movie_countries
(
    movie_id   INT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    country_id INT NOT NULL REFERENCES countries (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (movie_id, country_id)
);
--rollback DROP TABLE IF EXISTS movie_countries; DROP TABLE IF EXISTS countries;