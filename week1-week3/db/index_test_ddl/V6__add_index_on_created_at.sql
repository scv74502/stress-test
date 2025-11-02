use index_test_database;

-- 인덱스 없는 테이블: 여전히 테이블 전체를 스캔해야 함
EXPLAIN SELECT count(*) FROM article WHERE writer_nickname LIKE 'user399999%';
SELECT count(*) FROM article WHERE writer_nickname LIKE 'user399999%';

-- 인덱스 있는 테이블: 인덱스에서 소수의 범위만 빠르게 스캔
EXPLAIN SELECT count(*) FROM article_indexed WHERE writer_nickname LIKE 'user399999%';
SELECT count(*) FROM article_indexed WHERE writer_nickname LIKE 'user399999%';

-- ======================================================================
-- 시나리오: 최신 게시물 10개 조회 (정렬 성능 비교)
-- 결과: 수백 배 이상의 극적인 시간 차이 발생 예상
-- ======================================================================

-- 쿼리 프로파일링 활성화 (실행 시간 측정을 위해)
SET profiling = 1;

use index_test_database;
-- 1. 인덱스 없는 테이블: Full Scan + Full Sort (매우 느림)
-- 예상 동작:
--   1. 'article' 테이블 전체(47만 건)를 스캔하여 is_deleted = FALSE 인 약 42만 건을 찾습니다.
--   2. 찾아낸 약 42만 건의 데이터를 created_at 기준으로 전부 정렬합니다. (가장 비용이 큰 부분)
--   3. 정렬된 결과에서 상위 10개를 반환합니다.
EXPLAIN SELECT * FROM article WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10;
SELECT * FROM article WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10;


-- 2. 인덱스 있는 테이블: Index Backward Scan (매우 빠름)
-- 예상 동작:
--   1. 'created_at' 인덱스를 거꾸로(DESC) 탐색 시작합니다.
--   2. 인덱스에서 한 개씩 읽으며 is_deleted = FALSE 인지 확인합니다.
--   3. 조건에 맞는 데이터를 10개 찾으면 즉시 탐색을 멈추고 결과를 반환합니다.
EXPLAIN SELECT * FROM article_indexed WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10;
SELECT * FROM article_indexed WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10;


-- 실행 시간 비교
SHOW PROFILES;
