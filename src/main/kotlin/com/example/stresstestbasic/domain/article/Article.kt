package com.example.stresstestbasic.domain.article

import com.example.stresstestbasic.domain.AbstractEntity
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.time.Instant

@Table(name = "article")
class Article: AbstractEntity() {
    @NaturalId
    var writerNickname: String? = null

    var title: String? = null

    var context: String? = null

    var isDeleted: Boolean? = false

    var createdAt: Instant? = null

    var updatedAt: Instant? = null
}