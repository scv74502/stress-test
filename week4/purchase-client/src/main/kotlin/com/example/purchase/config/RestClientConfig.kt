package com.example.purchase.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import java.time.Duration

private val logger = KotlinLogging.logger {}

@Configuration
class RestClientConfig {

    @Value("\${payment-api.base-url}")
    private lateinit var baseUrl: String

    @Value("\${payment-api.connection-timeout}")
    private var connectionTimeout: Long = 5000

    @Value("\${payment-api.read-timeout}")
    private var readTimeout: Long = 10000

    @Bean
    fun paymentRestClient(): RestClient {
        logger.info { "RestClient 설정 - baseUrl: $baseUrl, connectionTimeout: ${connectionTimeout}ms, readTimeout: ${readTimeout}ms" }

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestInterceptor(loggingInterceptor())
            .requestFactory(org.springframework.http.client.JdkClientHttpRequestFactory().apply {
                setReadTimeout(Duration.ofMillis(readTimeout))
            })
            .build()
    }

    private fun loggingInterceptor(): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { request, body, execution ->
            val startTime = System.currentTimeMillis()
            logger.debug { "[HTTP Request] ${request.method} ${request.uri}" }

            try {
                val response = execution.execute(request, body)
                val duration = System.currentTimeMillis() - startTime
                logger.debug { "[HTTP Response] Status: ${response.statusCode}, Duration: ${duration}ms" }
                response
            } catch (e: Exception) {
                val duration = System.currentTimeMillis() - startTime
                logger.error { "[HTTP Error] ${e.javaClass.simpleName}: ${e.message}, Duration: ${duration}ms" }
                throw e
            }
        }
    }
}
