DROP PROCEDURE IF EXISTS equalize_records;

CREATE PROCEDURE equalize_records()
BEGIN
    DECLARE article_count INT;
    DECLARE indexed_count INT;
    DECLARE i INT;

    SELECT COUNT(*) INTO article_count FROM article;
    SELECT COUNT(*) INTO indexed_count FROM article_indexed;

    IF article_count > indexed_count THEN
        SET i = indexed_count + 1;
        WHILE i <= article_count DO
            INSERT INTO article_indexed (writer_nickname, title, content, is_deleted) VALUES (
                CONCAT('user', i),
                CONCAT('Title ', i),
                CONCAT('Content for article ', i),
                (i % 10 = 0)
            );
            SET i = i + 1;
        END WHILE;
    ELSEIF indexed_count > article_count THEN
        SET i = article_count + 1;
        WHILE i <= indexed_count DO
            INSERT INTO article (writer_nickname, title, content, is_deleted) VALUES (
                CONCAT('user', i),
                CONCAT('Title ', i),
                CONCAT('Content for article ', i),
                (i % 10 = 0)
            );
            SET i = i + 1;
        END WHILE;
    END IF;
END;
