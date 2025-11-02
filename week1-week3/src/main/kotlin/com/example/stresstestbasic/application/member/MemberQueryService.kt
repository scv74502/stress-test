package com.example.stresstestbasic.application.member

import com.example.stresstestbasic.application.member.provided.MemberFinder
import com.example.stresstestbasic.application.member.required.MemberRepository
import com.example.stresstestbasic.domain.member.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.lang.IllegalArgumentException

@Service
@Transactional
@Validated
class MemberQueryService(
    private val memberRepository: MemberRepository
): MemberFinder {
    override fun find(memberId: Long): Member {
        return memberRepository.findById(memberId)
            ?: throw IllegalArgumentException("$memberId 회원이 없습니다")
    }
}