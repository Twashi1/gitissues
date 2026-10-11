CREATE TABLE projects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL DEFAULT '',
    export_directory TEXT NOT NULL DEFAULT ''
);

-- Insert a default project
INSERT INTO projects (name, export_directory) VALUES ('Default Project', '.');

CREATE TABLE issue_lists (
    project_id INTEGER NOT NULL,
    id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, id),
    UNIQUE (project_id, title)
);

CREATE TABLE issues (
    uuid7 VARCHAR(36) NOT NULL PRIMARY KEY,
    project_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    list_id INTEGER,
    FOREIGN KEY (project_id, list_id) REFERENCES issue_lists(project_id, id) ON DELETE SET NULL
);
