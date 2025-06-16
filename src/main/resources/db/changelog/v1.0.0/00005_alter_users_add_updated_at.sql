--liquibase formatted sql

--changeset k1mb1:00005_alter_users_add_updated_at
ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
--rollback ALTER TABLE users DROP COLUMN IF EXISTS updated_at;