package com.example.stresstestbasic.application.articleTest

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Connection
import java.sql.DriverManager

@Service
@Transactional
class ArticleBatchInsertService(
    private val conn: Connection = DriverManager.getConnection(TODO("전역 설정 값 찾아서 넣기"))
) {
    fun batchInsert(amount: Long) {

    }
}