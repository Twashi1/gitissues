-- Create issue_lists table
CREATE TABLE issue_lists (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add list_id column to issues table
ALTER TABLE issues ADD COLUMN list_id BIGINT;

-- Add foreign key constraint
ALTER TABLE issues ADD CONSTRAINT fk_issues_list_id
    FOREIGN KEY (list_id) REFERENCES issue_lists(id);