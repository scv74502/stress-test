package com.example.stresstestbasic.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * application.yaml의 'my-service' 접두사를 가진 설정 값을 바인딩하는 클래스입니다.
 * 데이터 클래스를 사용하여 불변(immutable)으로 안전하게 관리합니다.
 */
@ConfigurationProperties(prefix = "my-service")
data class MyServiceProperties(
    // 기존 속성
    val apiKey: String,
    val timeout: Int,
    val urls: List<String>,

    // DB 연결 정보 추가
    val dbUrl: String,
    val user: String,
    val password: String
)
