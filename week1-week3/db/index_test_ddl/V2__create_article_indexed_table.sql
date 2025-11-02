CREATE TABLE article_indexed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_nickname VARCHAR(50),
    title VARCHAR(100),
    content TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX (writer_nickname),
    INDEX (is_deleted),
    INDEX (created_at)
);

SELECT COUNT(*) FROM article_indexed;
