package com.example.stresstestbasic

import com.example.stresstestbasic.config.MyServiceProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MyServiceProperties::class)
class StressTestBasicApplication

fun main(args: Array<String>) {
    runApplication<StressTestBasicApplication>(*args)
}
