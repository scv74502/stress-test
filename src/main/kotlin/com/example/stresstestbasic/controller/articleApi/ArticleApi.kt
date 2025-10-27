package com.example.stresstestbasic.controller.articleApi

import com.example.stresstestbasic.application.articleTest.ArticleBatchInsertService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleApi(
    private val articleBatchInsertService: ArticleBatchInsertService
){
    @PostMapping("/api/member-batch/{amount}")
    fun batchInsert(@PathVariable amount: Long){
        articleBatchInsertService.batchInsert(amount)
    }
}