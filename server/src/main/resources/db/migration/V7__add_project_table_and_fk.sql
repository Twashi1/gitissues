-- Drop existing tables to start fresh (since we can drop all old data)
DROP TABLE IF EXISTS issue_tags;
DROP TABLE IF EXISTS issue_lists;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS projects;

-- Create project table
CREATE TABLE projects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL DEFAULT '',
    export_directory TEXT NOT NULL DEFAULT ''
);

-- Insert a default project
INSERT INTO projects (name, export_directory) VALUES ('Default Project', '.');

-- Create issue_lists table (referenced by issues)
CREATE TABLE issue_lists (
    project_id INTEGER NOT NULL,
    id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, id),
    UNIQUE (project_id, title)
);

-- Create tags table
CREATE TABLE tags (
    project_id INTEGER NOT NULL,
    id INTEGER NOT NULL,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (project_id, id),
    UNIQUE (project_id, name)
);

-- Create issues table
CREATE TABLE issues (
    project_id INTEGER NOT NULL,
    id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    list_id INTEGER,
    PRIMARY KEY (project_id, id),
    FOREIGN KEY (project_id, list_id) REFERENCES issue_lists(project_id, id) ON DELETE SET NULL
);

-- Create issue_tags table
CREATE TABLE issue_tags (
    project_id INTEGER NOT NULL,
    issue_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    value TEXT,
    PRIMARY KEY (project_id, issue_id, tag_id),
    FOREIGN KEY (project_id, issue_id) REFERENCES issues(project_id, id) ON DELETE CASCADE,
    FOREIGN KEY (project_id, tag_id) REFERENCES tags(project_id, id) ON DELETE CASCADE
);