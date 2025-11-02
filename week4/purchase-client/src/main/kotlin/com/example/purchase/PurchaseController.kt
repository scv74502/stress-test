
package com.example.purchase

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/purchase")
class PurchaseController(
    private val paymentApiClient: PaymentApiClient
) {

    /**
     * 시나리오 1: 정상 주문 (Resilience4j Retry + Circuit Breaker 적용)
     * JMeter 테스트용
     */
    @PostMapping("/order")
    fun createOrder(@RequestBody request: OrderRequest): ResponseEntity<String> {
        return try {
            logger.info { "[주문 요청] userId: ${request.userId}, amount: ${request.amount}" }
            val response = paymentApiClient.processPayment(request)
            logger.info { "[주문 성공] transactionId: ${response.transactionId}" }
            ResponseEntity.ok("주문 완료: ${response.transactionId}")
        } catch (e: Exception) {
            logger.error { "[주문 실패] ${e.javaClass.simpleName}: ${e.message}" }
            ResponseEntity.status(500).body("주문 실패: ${e.message}")
        }
    }

    /**
     * 시나리오 2: Resilience4j 없이 주문 (재시도 및 Circuit Breaker 미적용)
     * 타임아웃 발생 시 바로 실패
     */
    @PostMapping("/order-no-resilience")
    fun createOrderWithoutResilience(@RequestBody request: OrderRequest): ResponseEntity<String> {
        return try {
            logger.info { "[주문 요청 - Resilience4j 없음] userId: ${request.userId}, amount: ${request.amount}" }
            val response = paymentApiClient.processPaymentWithoutResilience(request)
            logger.info { "[주문 성공 - Resilience4j 없음] transactionId: ${response.transactionId}" }
            ResponseEntity.ok("주문 완료: ${response.transactionId}")
        } catch (e: Exception) {
            logger.error { "[주문 실패 - Resilience4j 없음] ${e.javaClass.simpleName}: ${e.message}" }
            ResponseEntity.status(500).body("주문 실패: ${e.message}")
        }
    }

    /**
     * 시나리오 3: Read Timeout 테스트 (B서버가 20초 대기)
     * Read Timeout 설정값에 따라 타임아웃 발생
     */
    @PostMapping("/order-slow")
    fun createOrderSlow(@RequestBody request: OrderRequest): ResponseEntity<String> {
        return try {
            logger.info { "[주문 요청 - Slow] userId: ${request.userId}, amount: ${request.amount}" }
            val response = paymentApiClient.processPaymentSlow(request)
            logger.info { "[주문 성공 - Slow] transactionId: ${response.transactionId}" }
            ResponseEntity.ok("주문 완료 (20초 후): ${response.transactionId}")
        } catch (e: Exception) {
            logger.error { "[주문 실패 - Slow] ${e.javaClass.simpleName}: ${e.message}" }
            ResponseEntity.status(500).body("주문 실패 (Read Timeout 가능성): ${e.message}")
        }
    }

    /**
     * 시나리오 4: 지연 시간을 직접 지정하는 테스트
     * 다양한 지연 시간에 따른 타임아웃 테스트
     */
    @PostMapping("/order-delay/{seconds}")
    fun createOrderWithDelay(
        @PathVariable seconds: Long,
        @RequestBody request: OrderRequest
    ): ResponseEntity<String> {
        return try {
            logger.info { "[주문 요청 - Delay ${seconds}초] userId: ${request.userId}, amount: ${request.amount}" }
            val response = paymentApiClient.processPaymentWithDelay(request, seconds)
            logger.info { "[주문 성공 - Delay ${seconds}초] transactionId: ${response.transactionId}" }
            ResponseEntity.ok("주문 완료 (${seconds}초 후): ${response.transactionId}")
        } catch (e: Exception) {
            logger.error { "[주문 실패 - Delay ${seconds}초] ${e.javaClass.simpleName}: ${e.message}" }
            ResponseEntity.status(500).body("주문 실패: ${e.message}")
        }
    }

    /**
     * 헬스 체크 엔드포인트
     */
    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Purchase Client is running on port 8082")
    }
}
