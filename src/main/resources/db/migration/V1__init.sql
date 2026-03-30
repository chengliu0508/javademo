-- Flyway initialization for database `javademo`
-- Tables:
-- 1) app_user: user account data
-- 2) user_session: login session token (simple token-based auth)

CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(100) NULL,
  status INT NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_app_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS user_session (
  token CHAR(36) NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NOT NULL,
  PRIMARY KEY (token),
  KEY idx_user_session_user_id (user_id),
  CONSTRAINT fk_user_session_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

