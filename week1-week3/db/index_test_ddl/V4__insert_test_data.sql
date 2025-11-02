-- 기존 프로시저가 있다면 삭제
DROP PROCEDURE IF EXISTS insert_test_data;

-- 클라이언트가 세미콜론(;)을 프로시저의 끝으로 인식하지 않도록 구분자를 변경
DELIMITER $$

CREATE PROCEDURE insert_test_data()
BEGIN
    -- 반복을 위한 변수 선언
    DECLARE i INT DEFAULT 0;
    DECLARE j INT DEFAULT 0;
    DECLARE current_row_id BIGINT;

    -- 사용자가 요청한 배치 설정
    DECLARE max_batches INT DEFAULT 5000; -- 500번 반복 (5000)
    DECLARE batch_size INT DEFAULT 10000; -- 한 번에 10000건씩

    -- 동적으로 생성될 SQL 문을 담을 변수
    -- 충분한 크기를 할당 (10000건의 데이터는 매우 길어질 수 있음)
    DECLARE bulk_sql_article LONGTEXT;
    DECLARE bulk_sql_article_indexed LONGTEXT;

    -- 500번의 배치 반복 시작
    WHILE i < max_batches DO

        -- 'article' 테이블에 대한 벌크 인서트 SQL 문 생성 시작
        SET bulk_sql_article = 'INSERT INTO article (writer_nickname, title, content, is_deleted) VALUES ';
        
        -- 'article_indexed' 테이블에 대한 벌크 인서트 SQL 문 생성 시작
        SET bulk_sql_article_indexed = 'INSERT INTO article_indexed (writer_nickname, title, content, is_deleted) VALUES ';

        -- 한 배치(10000건)의 데이터를 만들기 위한 내부 반복
        SET j = 0;
        WHILE j < batch_size DO
            -- 현재 삽입될 데이터의 전체 ID 계산
            SET current_row_id = i * batch_size + j + 1;

            -- VALUES 절에 추가할 데이터 문자열 생성
            SET @row_values = CONCAT(
                '(\'user', current_row_id, '\',\'Title ', current_row_id, '\',\'Content for article ', current_row_id, '\',', (current_row_id % 10 = 0), ')'
            );

            -- 각 테이블의 SQL 문에 데이터 추가
            SET bulk_sql_article = CONCAT(bulk_sql_article, @row_values);
            SET bulk_sql_article_indexed = CONCAT(bulk_sql_article_indexed, @row_values);

            -- 마지막 데이터가 아니면 쉼표(,) 추가
            IF j < batch_size - 1 THEN
                SET bulk_sql_article = CONCAT(bulk_sql_article, ',');
                SET bulk_sql_article_indexed = CONCAT(bulk_sql_article_indexed, ',');
            END IF;

            SET j = j + 1;
        END WHILE;

        -- 10000건의 데이터가 담긴 SQL 문을 준비하고 실행 (article)
        SET @s = bulk_sql_article;
        PREPARE stmt FROM @s;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        -- 10000건의 데이터가 담긴 SQL 문을 준비하고 실행 (article_indexed)
        SET @s = bulk_sql_article_indexed;
        PREPARE stmt FROM @s;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;

END$$

-- 구분자를 다시 세미콜론(;)으로 원복
DELIMITER ;

CALL insert_test_data;