-- V3: Add entity field to issues table
-- uuid7 column is now added in V1 migration
ALTER TABLE issues ADD COLUMN entity INTEGER;