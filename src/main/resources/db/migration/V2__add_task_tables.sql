-- Task module tables
-- mode:   PARALLEL(并行) | COLLABORATIVE(协作)
-- status: PENDING | IN_PROGRESS | COMPLETED | CANCELLED

CREATE TABLE IF NOT EXISTS task (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  title       VARCHAR(200) NOT NULL,
  content     TEXT,
  creator_id  BIGINT       NOT NULL,
  mode        VARCHAR(20)  NOT NULL DEFAULT 'COLLABORATIVE',
  status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
  created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_task_creator_id (creator_id),
  KEY idx_task_status     (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- assignee status: PENDING | COMPLETED | REJECTED
CREATE TABLE IF NOT EXISTS task_assignee (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  task_id      BIGINT      NOT NULL,
  assignee_id  BIGINT      NOT NULL,
  status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  feedback     TEXT,
  completed_at TIMESTAMP   NULL DEFAULT NULL,
  created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP   NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_task_assignee_task_id     (task_id),
  KEY idx_task_assignee_assignee_id (assignee_id),
  CONSTRAINT fk_task_assignee_task
    FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
