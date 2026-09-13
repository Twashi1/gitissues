CREATE TABLE issue_tags (
  issue_id INTEGER NOT NULL,
  tag_id INTEGER NOT NULL,
  value TEXT,

  PRIMARY KEY (issue_id, tag_id),

  CONSTRAINT fk_issue_tags_issue
    FOREIGN KEY (issue_id)
    REFERENCES issues(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_issue_tags_tag
    FOREIGN KEY (tag_id)
    REFERENCES tags(id)
    ON DELETE CASCADE
);
