package com.example.stresstestbasic.domain.article

import com.example.stresstestbasic.domain.AbstractEntity
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.time.Instant

@Table(name = "article_indexed", indexes = [
    Index(name = "writer_nickname_idx", columnList = "writer_nickname"),
    Index(name = "is_deleted_idx", columnList = "is_deleted"),
    Index(name = "created_at_idx", columnList = "created_at")
])
class ArticleIndexed: AbstractEntity() {
    @NaturalId
    var writerNickname: String? = null

    var title: String? = null

    var context: String? = null

    var isDeleted: Boolean? = false

    var createdAt: Instant? = null

    var updatedAt: Instant? = null
}