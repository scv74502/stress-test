package com.example.stresstestbasic.application.member.required

import com.example.stresstestbasic.domain.member.Member
import com.example.stresstestbasic.domain.member.Profile
import com.example.stresstestbasic.domain.shared.Email
import org.springframework.data.repository.Repository

interface MemberRepository: Repository<Member, Long> {
    fun save(member: Member): Member
    fun findByEmailValue(email: Email): Member?
    fun findById(memberId: Long): Member?
}