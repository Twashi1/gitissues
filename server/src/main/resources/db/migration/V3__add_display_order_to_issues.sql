-- V3: Add display_order column to issues table for list sorting
ALTER TABLE issues ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

-- Index for efficient sorting by display order within a project/list
CREATE INDEX IF NOT EXISTS idx_issues_display_order ON issues(project_id, display_order);