package com.example.purchase.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.RetryRegistry
import org.springframework.context.annotation.Configuration
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent
import jakarta.annotation.PostConstruct
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnSuccessEvent
import io.github.resilience4j.retry.event.RetryOnErrorEvent
import io.github.resilience4j.retry.event.RetryOnRetryEvent
import io.github.resilience4j.retry.event.RetryOnSuccessEvent

private val logger = KotlinLogging.logger {}

@Configuration
class Resilience4jConfig(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val retryRegistry: RetryRegistry
) {

    @PostConstruct
    fun configureCircuitBreakerEventLogging() {
        circuitBreakerRegistry.allCircuitBreakers.forEach { circuitBreaker ->
            circuitBreaker.eventPublisher
                .onSuccess { event: CircuitBreakerOnSuccessEvent ->
                    logger.info { "[Circuit Breaker: ${event.circuitBreakerName}] 성공 - Duration: ${event.elapsedDuration.toMillis()}ms" }
                }
                .onError { event: CircuitBreakerOnErrorEvent ->
                    logger.error { "[Circuit Breaker: ${event.circuitBreakerName}] 실패 - Error: ${event.throwable.javaClass.simpleName}, Duration: ${event.elapsedDuration.toMillis()}ms" }
                }
                .onStateTransition { event: CircuitBreakerOnStateTransitionEvent ->
                    logger.warn { "[Circuit Breaker: ${event.circuitBreakerName}] 상태 전환 - ${event.stateTransition.fromState} → ${event.stateTransition.toState}" }
                }
        }
    }

    @PostConstruct
    fun configureRetryEventLogging() {
        retryRegistry.allRetries.forEach { retry ->
            retry.eventPublisher
                .onRetry { event: RetryOnRetryEvent ->
                    logger.warn { "[Retry: ${event.name}] 재시도 ${event.numberOfRetryAttempts}회 - Error: ${event.lastThrowable.javaClass.simpleName}: ${event.lastThrowable.message}" }
                }
                .onSuccess { event: RetryOnSuccessEvent ->
                    logger.info { "[Retry: ${event.name}] 성공 - 총 시도 횟수: ${event.numberOfRetryAttempts}" }
                }
                .onError { event: RetryOnErrorEvent ->
                    logger.error { "[Retry: ${event.name}] 모든 재시도 실패 - 총 시도 횟수: ${event.numberOfRetryAttempts}, 최종 에러: ${event.lastThrowable.javaClass.simpleName}" }
                }
        }
    }
}
