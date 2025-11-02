-- 쿼리 프로파일링 활성화
SET profiling = 1;

-- ====================================================================
-- 시나리오 1: writer_nickname으로 특정 데이터 검색 (고유성 높은 인덱스)
-- ====================================================================

-- 1-1. 인덱스 없는 테이블 (Full Table Scan)
EXPLAIN SELECT * FROM article WHERE writer_nickname = 'user500000';
SELECT * FROM article WHERE writer_nickname = 'user500000';

-- 1-2. 인덱스 있는 테이블 (Index Seek)
EXPLAIN SELECT * FROM article_indexed WHERE writer_nickname = 'user500000';
SELECT * FROM article_indexed WHERE writer_nickname = 'user500000';


-- ====================================================================
-- 시나리오 2: is_deleted로 데이터 필터링 (고유성 낮은 인덱스)
-- ====================================================================

-- 2-1. 인덱스 없는 테이블 (Full Table Scan)
EXPLAIN SELECT * FROM article WHERE is_deleted = TRUE;
SELECT * FROM article WHERE is_deleted = TRUE;

-- 2-2. 인덱스 있는 테이블 (Index Seek)
EXPLAIN SELECT * FROM article_indexed WHERE is_deleted = TRUE;
SELECT * FROM article_indexed WHERE is_deleted = TRUE;


-- ====================================================================
-- 시나리오 3: 복합 조건 검색
-- ====================================================================

-- 3-1. 인덱스 없는 테이블 (Full Table Scan)
EXPLAIN SELECT * FROM article WHERE writer_nickname = 'user700000' AND is_deleted = FALSE;
SELECT * FROM article WHERE writer_nickname = 'user700000' AND is_deleted = FALSE;

-- 3-2. 인덱스 있는 테이블 (Index Seek)
EXPLAIN SELECT * FROM article_indexed WHERE writer_nickname = 'user700000' AND is_deleted = FALSE;
SELECT * FROM article_indexed WHERE writer_nickname = 'user700000' AND is_deleted = FALSE;


-- ====================================================================
-- 시나리오 4: created_at 범위 검색 (레인지 스캔)
-- ====================================================================

-- 4-1. 인덱스 없는 테이블 (Full Table Scan)
EXPLAIN SELECT * FROM article WHERE created_at BETWEEN '2023-01-01 00:00:00' AND '2023-01-01 01:00:00';
SELECT * FROM article WHERE created_at BETWEEN '2023-01-01 00:00:00' AND '2023-01-01 01:00:00';

-- 4-2. 인덱스 있는 테이블 (Index Range Scan)
EXPLAIN SELECT * FROM article_indexed WHERE created_at BETWEEN '2023-01-01 00:00:00' AND '2023-01-01 01:00:00';
SELECT * FROM article_indexed WHERE created_at BETWEEN '2023-01-01 00:00:00' AND '2023-01-01 01:00:00';


-- ====================================================================
-- 시나리오 5: 정렬 (ORDER BY) 성능 비교
-- ====================================================================

-- 5-1. 인덱스 없는 컬럼으로 정렬 (Filesort)
EXPLAIN SELECT * FROM article WHERE is_deleted = FALSE ORDER BY title DESC LIMIT 10;
SELECT * FROM article WHERE is_deleted = FALSE ORDER BY title DESC LIMIT 10;

-- 5-2. 인덱스 있는 컬럼으로 정렬 (인덱스 순방향/역방향 스캔)
EXPLAIN SELECT * FROM article_indexed WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10;
SELECT * FROM article_indexed WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10;


-- ====================================================================
-- 프로파일링 결과 확인
-- ====================================================================
SHOW PROFILES;
