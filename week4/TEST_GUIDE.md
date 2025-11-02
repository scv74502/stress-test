# 4주차 타임아웃 테스트 가이드

## 프로젝트 구조

```
week4/
├── payment-api/          # B서버 (Payment API Service) - 포트 8081
├── purchase-client/      # A서버 (Purchase Client Service) - 포트 8082
└── TEST_GUIDE.md        # 이 파일
```

## 서버 실행 방법

### 1. Payment API 서버 실행 (B서버 - 포트 8081)

```bash
cd payment-api
../gradlew bootRun
```

또는 루트 디렉토리에서:
```bash
./gradlew :payment-api:bootRun
```

### 2. Purchase Client 서버 실행 (A서버 - 포트 8082)

```bash
cd purchase-client
../gradlew bootRun
```

또는 루트 디렉토리에서:
```bash
./gradlew :purchase-client:bootRun
```

### 3. 빌드

```bash
./gradlew build
```

---

## 설정 변경 방법

### 타임아웃 설정 변경 (purchase-client/src/main/resources/application.yaml)

```yaml
payment-api:
  connection-timeout: 5000  # Connection Timeout (밀리초)
  read-timeout: 10000       # Read Timeout (밀리초)
```

### Resilience4j 설정 변경

#### Retry 설정
```yaml
resilience4j:
  retry:
    instances:
      paymentApi:
        max-attempts: 3                      # 최대 재시도 횟수
        wait-duration: 2000                  # 재시도 간 대기 시간 (밀리초)
        enable-exponential-backoff: true     # 지수 백오프 활성화
        exponential-backoff-multiplier: 2    # 백오프 배수
```

#### Circuit Breaker 설정
```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentApi:
        failure-rate-threshold: 50           # 실패율 임계값 (%)
        sliding-window-size: 10              # 슬라이딩 윈도우 크기
        wait-duration-in-open-state: 10000   # OPEN 상태 유지 시간 (밀리초)
```

---

## JMeter 테스트 시나리오

### 시나리오 1: Connection Timeout 테스트

**목적**: 서버에 연결 자체가 안 되는 상황 시뮬레이션

**설정 방법**:
1. `purchase-client/src/main/resources/application.yaml` 수정
   ```yaml
   payment-api:
     base-url: http://10.255.255.1:8081  # 접근 불가능한 IP
     connection-timeout: 3000  # 3초, 5초, 10초로 변경하며 테스트
   ```
2. purchase-client 재시작
3. JMeter 설정:
   - Thread Group: 100 threads
   - HTTP Request: POST http://localhost:8082/purchase/order
   - Body Data:
     ```json
     {
       "userId": 1,
       "amount": 10000.0
     }
     ```

**측정 항목**:
- 타임아웃 발생 시간
- 전체 요청 처리 시간
- 실패율
- 로그 확인: Connection timeout 에러 메시지

---

### 시나리오 2: Read Timeout 테스트

**목적**: 연결은 성공했지만 응답이 늦는 상황 시뮬레이션

#### 2-1. Resilience4j 없이 테스트

**JMeter 설정**:
- Thread Group: 100 threads
- HTTP Request: POST http://localhost:8082/purchase/order-no-resilience
- Body Data:
  ```json
  {
    "userId": 1,
    "amount": 10000.0
  }
  ```

**purchase-client 설정 변경**:
```yaml
payment-api:
  base-url: http://localhost:8081
  read-timeout: 3000  # 3초, 5초, 10초로 변경하며 테스트
```

**호출 엔드포인트 변경** (B서버에서 20초 지연):
- JMeter에서 `/purchase/order-slow` 호출

**측정 항목**:
- Read Timeout 발생 시간
- 클라이언트 로그: 타임아웃 발생 시각
- 서버 로그: 타임아웃 후에도 처리가 계속되는지 확인

#### 2-2. Resilience4j Retry 적용

**JMeter 설정**:
- HTTP Request: POST http://localhost:8082/purchase/order (Resilience4j 적용)

**purchase-client 설정**:
```yaml
resilience4j:
  retry:
    instances:
      paymentApi:
        max-attempts: 3
        wait-duration: 2000
```

**측정 항목**:
- 재시도 횟수 (로그 확인)
- 총 처리 시간 (재시도 포함)
- 재시도 간 대기 시간

#### 2-3. Circuit Breaker 적용

**연속 실패 시나리오**:
1. JMeter로 100개 요청 전송 (대부분 실패)
2. Circuit Breaker OPEN 상태로 전환 확인 (로그)
3. 10초 대기 후 Half-Open 상태 확인
4. 정상 응답 시 CLOSED 상태로 복구 확인

**측정 항목**:
- Circuit Breaker 상태 전환 로그
- OPEN 상태에서 빠른 실패 (fail-fast) 확인
- 복구 시간

---

### 시나리오 3: 다양한 지연 시간 테스트

**JMeter 설정**:
- HTTP Request: POST http://localhost:8082/purchase/order-delay/{seconds}
- 경로 변수: `{seconds}` = 1, 3, 5, 10, 15, 20

**예시**:
- 3초 지연: POST http://localhost:8082/purchase/order-delay/3
- 15초 지연: POST http://localhost:8082/purchase/order-delay/15

---

## 로그 확인 방법

### Purchase Client (A서버) 로그

```
[결제 API 호출] 시작 - userId: 1, amount: 10000.0, 시각: 2025-11-02 12:00:00.000
[HTTP Request] POST http://localhost:8081/payment/process
[HTTP Response] Status: 200 OK, Duration: 50ms
[결제 API 호출] 성공 - transactionId: TXN_1234567890, 시각: 2025-11-02 12:00:00.050
```

**타임아웃 발생 시**:
```
[결제 API 호출] 실패 - Error: ResourceAccessException: Read timed out, 시각: 2025-11-02 12:00:10.000
[Retry: paymentApi] 재시도 1회 - Error: ResourceAccessException: Read timed out
```

**Circuit Breaker 상태 전환 시**:
```
[Circuit Breaker: paymentApi] 상태 전환 - CLOSED → OPEN
[Circuit Breaker: paymentApi] 상태 전환 - OPEN → HALF_OPEN
[Circuit Breaker: paymentApi] 상태 전환 - HALF_OPEN → CLOSED
```

### Payment API (B서버) 로그

```
[정상 처리] 요청 수신 - userId: 1, amount: 10000.0, 시각: 2025-11-02 12:00:00.000
[정상 처리] 처리 완료 - transactionId: TXN_1234567890, 시각: 2025-11-02 12:00:00.010
```

**20초 지연 처리 시**:
```
[지연 처리 20초] 요청 수신 - userId: 1, amount: 10000.0, 시각: 2025-11-02 12:00:00.000
결제 처리 시작... 20초 대기
결제 처리 완료
[지연 처리 20초] 처리 완료 - transactionId: TXN_1234567890, 시각: 2025-11-02 12:00:20.000 (클라이언트가 타임아웃 되었어도 이 로그가 출력됨)
```

---

## API 엔드포인트 정리

### Purchase Client (A서버 - 포트 8082)

| 엔드포인트 | 메서드 | 설명 | Resilience4j |
|----------|--------|------|--------------|
| `/purchase/order` | POST | 정상 주문 | O |
| `/purchase/order-no-resilience` | POST | Resilience4j 없는 주문 | X |
| `/purchase/order-slow` | POST | 20초 지연 응답 테스트 | X |
| `/purchase/order-delay/{seconds}` | POST | 지정 시간 지연 테스트 | X |
| `/purchase/health` | GET | 헬스 체크 | - |

### Payment API (B서버 - 포트 8081)

| 엔드포인트 | 메서드 | 설명 | 지연 시간 |
|----------|--------|------|----------|
| `/payment/process` | POST | 정상 처리 | 즉시 |
| `/payment/process-slow` | POST | 지연 처리 | 20초 |
| `/payment/process-delay/{seconds}` | POST | 지정 시간 지연 | N초 |

---

## 테스트 보고서 작성 가이드

### 1. Connection Timeout 테스트

**테스트 설정**:
- Connection Timeout: 3초, 5초, 10초
- 동시 요청 수: 100개

**측정 항목**:
- 타임아웃 발생 시간
- 평균 응답 시간
- 에러율
- 클라이언트 스레드 상태

### 2. Read Timeout 테스트

**테스트 설정**:
- Read Timeout: 3초, 5초, 10초
- 서버 응답 지연: 20초
- 동시 요청 수: 100개

**측정 항목**:
- 타임아웃 발생 시간
- 서버에서 처리 계속 여부 확인
- Retry 적용 시 재시도 횟수 및 총 시간
- Circuit Breaker 동작 여부

### 3. 재시도 정책 분석

**질문**:
- 재시도를 해도 괜찮을까?
- 최종 구매 처리를 할 수 있도록 재처리하는 방법은?
- 재시도를 하지 않았는데 구매 처리가 진행됐다면 어떻게 해야 할까?

**권장 답변 방향**:
- 멱등성(Idempotency) 보장 방법
- 트랜잭션 ID 기반 중복 처리 방지
- 보상 트랜잭션(Compensation Transaction) 처리

---

## 트러블슈팅

### 문제: Connection refused

**원인**: B서버가 실행되지 않음

**해결**: payment-api 서버를 먼저 실행

---

### 문제: 빌드 실패

**원인**: Gradle 캐시 문제

**해결**:
```bash
./gradlew clean build --refresh-dependencies
```

---

### 문제: Resilience4j가 동작하지 않음

**원인**: AOP 설정 누락

**해결**: build.gradle.kts에 `spring-boot-starter-aop` 의존성 확인

---

## 참고 자료

- [Resilience4j 공식 문서](https://resilience4j.readme.io/)
- [Spring RestClient 문서](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html)
- [Connection Timeout 시뮬레이션 (StackOverflow)](https://stackoverflow.com/questions/100841/artificially-create-a-connection-timeout-error)
