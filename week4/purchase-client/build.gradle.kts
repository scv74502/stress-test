// purchase-client 모듈 (A서버 - Purchase Client Service)
// Resilience4j를 사용한 Circuit Breaker, Retry, TimeLimiter 구현

dependencies {
    // Resilience4j - Spring Boot 3 통합
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Resilience4j - Reactor 지원 (비동기)
    implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")
}
