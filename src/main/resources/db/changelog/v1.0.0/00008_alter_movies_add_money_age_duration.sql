--liquibase formatted sql

--changeset k1mb1:00008_alter_movies_add_money_age_duration
ALTER TABLE movies
    ADD COLUMN world_gross BIGINT,
    ADD COLUMN budget BIGINT,
    ADD COLUMN age_rating VARCHAR(10),
    ADD COLUMN duration_minutes INT;
--rollback ALTER TABLE movies DROP COLUMN IF EXISTS world_gross, DROP COLUMN IF EXISTS budget, DROP COLUMN IF EXISTS age_rating, DROP COLUMN IF EXISTS duration_minutes;