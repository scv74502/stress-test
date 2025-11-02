create database index_test_database;
use index_test_database;

CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_nickname VARCHAR(50),
    title VARCHAR(100),
    content TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

SELECT COUNT(*) FROM article;