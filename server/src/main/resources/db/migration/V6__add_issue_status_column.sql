-- Add status column back to issues table
ALTER TABLE issues ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'open';