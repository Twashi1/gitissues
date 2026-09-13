-- Create issue_lists table
CREATE TABLE issue_lists (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add list_id column to issues table with foreign key reference
ALTER TABLE issues ADD COLUMN list_id INTEGER REFERENCES issue_lists(id);