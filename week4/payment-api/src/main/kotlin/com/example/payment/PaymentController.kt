
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
}
