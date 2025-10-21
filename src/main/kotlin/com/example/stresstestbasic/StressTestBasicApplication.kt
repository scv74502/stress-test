package com.example.stresstestbasic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StressTestBasicApplication

fun main(args: Array<String>) {
    runApplication<StressTestBasicApplication>(*args)
}
