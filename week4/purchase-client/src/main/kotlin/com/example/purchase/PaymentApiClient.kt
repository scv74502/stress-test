
package com.example.purchase

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

@Component
class PaymentApiClient(
    private val paymentRestClient: RestClient
) {

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    @Retry(name = "paymentApi", fallbackMethod = "processPaymentFallback")
    @CircuitBreaker(name = "paymentApi", fallbackMethod = "processPaymentFallback")
    fun processPayment(request: OrderRequest): PaymentResponse {
        val startTime = LocalDateTime.now()
        logger.info { "[결제 API 호출] 시작 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        try {
            val response = paymentRestClient.post()
                .uri("/payment/process")
                .body(request)
                .retrieve()
                .body(PaymentResponse::class.java)!!

            val endTime = LocalDateTime.now()
            logger.info { "[결제 API 호출] 성공 - transactionId: ${response.transactionId}, 시각: ${endTime.format(timeFormatter)}" }

            return response
        } catch (e: Exception) {
            val endTime = LocalDateTime.now()
            logger.error { "[결제 API 호출] 실패 - Error: ${e.javaClass.simpleName}: ${e.message}, 시각: ${endTime.format(timeFormatter)}" }
            throw e
        }
    }

    fun processPaymentWithoutResilience(request: OrderRequest): PaymentResponse {
        val startTime = LocalDateTime.now()
        logger.info { "[결제 API 호출 - Resilience4j 없음] 시작 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        try {
            val response = paymentRestClient.post()
                .uri("/payment/process")
                .body(request)
                .retrieve()
                .body(PaymentResponse::class.java)!!

            val endTime = LocalDateTime.now()
            logger.info { "[결제 API 호출 - Resilience4j 없음] 성공 - transactionId: ${response.transactionId}, 시각: ${endTime.format(timeFormatter)}" }

            return response
        } catch (e: Exception) {
            val endTime = LocalDateTime.now()
            logger.error { "[결제 API 호출 - Resilience4j 없음] 실패 - Error: ${e.javaClass.simpleName}: ${e.message}, 시각: ${endTime.format(timeFormatter)}" }
            throw e
        }
    }

    fun processPaymentSlow(request: OrderRequest): PaymentResponse {
        val startTime = LocalDateTime.now()
        logger.info { "[결제 API 호출 - Slow] 시작 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        try {
            val response = paymentRestClient.post()
                .uri("/payment/process-slow")
                .body(request)
                .retrieve()
                .body(PaymentResponse::class.java)!!

            val endTime = LocalDateTime.now()
            logger.info { "[결제 API 호출 - Slow] 성공 - transactionId: ${response.transactionId}, 시각: ${endTime.format(timeFormatter)}" }

            return response
        } catch (e: Exception) {
            val endTime = LocalDateTime.now()
            logger.error { "[결제 API 호출 - Slow] 실패 - Error: ${e.javaClass.simpleName}: ${e.message}, 시각: ${endTime.format(timeFormatter)}" }
            throw e
        }
    }

    fun processPaymentWithDelay(request: OrderRequest, seconds: Long): PaymentResponse {
        val startTime = LocalDateTime.now()
        logger.info { "[결제 API 호출 - Delay ${seconds}초] 시작 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        try {
            val response = paymentRestClient.post()
                .uri("/payment/process-delay/$seconds")
                .body(request)
                .retrieve()
                .body(PaymentResponse::class.java)!!

            val endTime = LocalDateTime.now()
            logger.info { "[결제 API 호출 - Delay ${seconds}초] 성공 - transactionId: ${response.transactionId}, 시각: ${endTime.format(timeFormatter)}" }

            return response
        } catch (e: Exception) {
            val endTime = LocalDateTime.now()
            logger.error { "[결제 API 호출 - Delay ${seconds}초] 실패 - Error: ${e.javaClass.simpleName}: ${e.message}, 시각: ${endTime.format(timeFormatter)}" }
            throw e
        }
    }

    private fun processPaymentFallback(request: OrderRequest, throwable: Throwable): PaymentResponse {
        logger.error { "[결제 API Fallback] Circuit Breaker 또는 Retry 최종 실패 - Error: ${throwable.javaClass.simpleName}: ${throwable.message}" }
        throw throwable
    }
}
