package com.example.stresstestbasic.domain.member

import com.example.stresstestbasic.domain.AbstractEntity
import jakarta.persistence.Entity
import org.springframework.util.Assert
import java.time.LocalDateTime
import java.util.*

@Entity
class MemberDetail : AbstractEntity() {
    private var profileAddress: String? = null

    private var introduction: String? = null

    private var registeredAt: LocalDateTime? = null

    private var activatedAt: LocalDateTime? = null

    private var deactivatedAt: LocalDateTime? = null

    fun activate() {
        Assert.isTrue(activatedAt == null, "이미 activatedAt은 설정되었습니다")

        this.activatedAt = LocalDateTime.now()
    }

    fun deactivate() {
        Assert.isTrue(deactivatedAt == null, "이미 deactivatedAt은 설정되었습니다")

        this.deactivatedAt = LocalDateTime.now()
    }

    fun updateInfo(updateRequest: MemberInfoUpdateRequest) {
        // Profile 검증
        Profile(updateRequest.profileAddress) // 유효성 검증용
        this.profileAddress = updateRequest.profileAddress
        this.introduction = Objects.requireNonNull(updateRequest.introduction)
    }

    companion object {
        fun create(): MemberDetail {
            val memberDetail = MemberDetail()
            memberDetail.registeredAt = LocalDateTime.now()
            return memberDetail
        }
    }
}
