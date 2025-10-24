package com.example.stresstestbasic.domain.member

import com.example.stresstestbasic.domain.AbstractEntity
import com.example.stresstestbasic.domain.shared.Email
import jakarta.persistence.*
import org.hibernate.annotations.NaturalId
import org.springframework.util.Assert
import java.util.*

@Entity
class Member : AbstractEntity() {
    @NaturalId
    @Column(unique = true, nullable = false, name = "email")
    private var emailValue: String? = null

    private var nickname: String? = null

    private var passwordHash: String? = null

    @Enumerated(EnumType.STRING)
    private var status: MemberStatus? = null

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "member_detail_id")
    private var detail: MemberDetail? = null

    // Value class getter/setter
    var email: Email?
        get() = emailValue?.let { Email(it) }
        set(value) {
            emailValue = value?.email
        }

    fun activate() {
        Assert.state(status === MemberStatus.PENDING, "PENDING 상태가 아닙니다")
        this.status = MemberStatus.ACTIVE
        this.detail?.activate()
    }

    fun deactivate() {
        Assert.state(status === MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다")

        this.status = MemberStatus.DEACTIVATED
        this.detail?.deactivate()
    }

    fun verifyPassword(password: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(password, this.passwordHash!!)
    }

    fun updateInfo(updateRequest: MemberInfoUpdateRequest) {
        Assert.state(status === MemberStatus.ACTIVE, "등록 완료 상태가 아니면 정보를 수정할 수 없습니다")

        this.nickname = updateRequest.nickname

        this.detail?.updateInfo(updateRequest)
    }

    fun changePassword(password: String, passwordEncoder: PasswordEncoder) {
        this.passwordHash = passwordEncoder.encode(password)
    }

    val isActive: Boolean
        get() = this.status === MemberStatus.ACTIVE

    companion object {
        fun register(createRequest: MemberRegisterRequest, passwordEncoder: PasswordEncoder): Member {
            val member = Member()

            // Email 검증
            Email(createRequest.email) // 유효성 검증용
            member.email = Email(createRequest.email)
            member.nickname = Objects.requireNonNull(createRequest.nickname)
            member.passwordHash = Objects.requireNonNull(passwordEncoder.encode(createRequest.password))

            member.status = MemberStatus.PENDING

            member.detail = MemberDetail.create()

            return member
        }
    }
}