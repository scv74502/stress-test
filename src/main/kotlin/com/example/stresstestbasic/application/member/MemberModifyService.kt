package com.example.stresstestbasic.application.member

import com.example.stresstestbasic.application.member.provided.MemberFinder
import com.example.stresstestbasic.application.member.provided.MemberRegister
import com.example.stresstestbasic.application.member.required.MemberQueryRepository
import com.example.stresstestbasic.application.member.required.MemberRepository
import com.example.stresstestbasic.domain.member.Member
import com.example.stresstestbasic.domain.member.MemberInfoUpdateRequest
import com.example.stresstestbasic.domain.member.MemberRegisterRequest
import com.example.stresstestbasic.domain.member.PasswordEncoder
import com.example.stresstestbasic.domain.member.Profile
import com.example.stresstestbasic.domain.member.exception.DuplicateEmailException
import com.example.stresstestbasic.domain.member.exception.DuplicateProfileException
import com.example.stresstestbasic.domain.shared.Email
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Transactional
@Validated
class MemberModifyService(
    private val memberFinder: MemberFinder,
    private val memberRepository: MemberRepository,
    private val memberQueryRepository: MemberQueryRepository,
//    private val passwordEncoder: PasswordEncoder
): MemberRegister {
    override fun register(registerRequest: MemberRegisterRequest): Member {
        TODO("Not yet implemented")
    }

    override fun activate(memberId: Long): Member {
        TODO("Not yet implemented")
    }

    override fun deactivate(memberId: Long): Member {
        TODO("Not yet implemented")
    }

    override fun updateInfo(
        memberId: Long,
        memberInfoUpdateRequest: @Valid MemberInfoUpdateRequest
    ): Member {
        TODO("Not yet implemented")
    }

    private fun checkDuplicateProfile(member: Member, profileAddress: String) {
        if (profileAddress.isEmpty()) return

        val currentProfileAddress: String? = member.detail?.profileAddress
        if (currentProfileAddress != null && currentProfileAddress == profileAddress) return

        if (memberQueryRepository.findByProfile(Profile(profileAddress)) != null) {
            throw DuplicateProfileException("이미 존재하는 프로필 주소입니다: $profileAddress")
        }
    }

    private fun checkDuplicateEmail(registerRequest: MemberRegisterRequest) {
        memberRepository.findByEmailValue(Email(registerRequest.email)) ?:
            throw DuplicateEmailException("이미 사용중인 이메일입니다: " + registerRequest.email)
    }

}