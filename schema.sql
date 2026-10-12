-- Optional: Hibernate (ddl-auto=update) creates these automatically. Provided for reference.
CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE TABLE accounts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_number VARCHAR(20) NOT NULL UNIQUE,
  holder_name VARCHAR(255) NOT NULL,
  account_type VARCHAR(20) NOT NULL,
  balance DECIMAL(19,2) NOT NULL DEFAULT 0,
  owner_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(20) NOT NULL,
  from_account VARCHAR(255),
  to_account VARCHAR(255),
  amount DECIMAL(19,2) NOT NULL,
  description VARCHAR(255),
  created_at DATETIME NOT NULL,
  INDEX idx_tx_from (from_account),
  INDEX idx_tx_to (to_account)
);
