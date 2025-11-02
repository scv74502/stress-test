
package com.example.payment

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/payment")
class PaymentController {

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    @PostMapping("/process")
    fun processPayment(@RequestBody request: PaymentRequest): ResponseEntity<PaymentResponse> {
        val startTime = LocalDateTime.now()
        logger.info { "[정상 처리] 요청 수신 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        val transactionId = "TXN_${System.currentTimeMillis()}"
        val response = PaymentResponse("SUCCESS", transactionId)

        val endTime = LocalDateTime.now()
        logger.info { "[정상 처리] 처리 완료 - transactionId: $transactionId, 시각: ${endTime.format(timeFormatter)}" }

        return ResponseEntity.ok(response)
    }

    @PostMapping("/process-slow")
    fun processPaymentSlow(@RequestBody request: PaymentRequest): ResponseEntity<PaymentResponse> {
        val startTime = LocalDateTime.now()
        logger.info { "[지연 처리 20초] 요청 수신 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        simulateDelay(20)

        val transactionId = "TXN_${System.currentTimeMillis()}"
        val response = PaymentResponse("SUCCESS", transactionId)

        val endTime = LocalDateTime.now()
        logger.info { "[지연 처리 20초] 처리 완료 - transactionId: $transactionId, 시각: ${endTime.format(timeFormatter)} (클라이언트가 타임아웃 되었어도 이 로그가 출력됨)" }

        return ResponseEntity.ok(response)
    }

    @PostMapping("/process-delay/{seconds}")
    fun processPaymentWithDelay(
        @PathVariable seconds: Long,
        @RequestBody request: PaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val startTime = LocalDateTime.now()
        logger.info { "[지연 처리 ${seconds}초] 요청 수신 - userId: ${request.userId}, amount: ${request.amount}, 시각: ${startTime.format(timeFormatter)}" }

        simulateDelay(seconds)

        val transactionId = "TXN_${System.currentTimeMillis()}"
        val response = PaymentResponse("SUCCESS", transactionId)

        val endTime = LocalDateTime.now()
        logger.info { "[지연 처리 ${seconds}초] 처리 완료 - transactionId: $transactionId, 시각: ${endTime.format(timeFormatter)}" }

        return ResponseEntity.ok(response)
    }

    private fun simulateDelay(seconds: Long) {
        try {
            logger.debug { "결제 처리 시작... ${seconds}초 대기" }
            Thread.sleep(seconds * 1000)
            logger.debug { "결제 처리 완료" }
        } catch (e: InterruptedException) {
            logger.warn { "처리 중 인터럽트 발생" }
            Thread.currentThread().interrupt()
        }
    }
}
