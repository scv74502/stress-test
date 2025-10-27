package com.example.stresstestbasic.application.member.provided

import com.example.stresstestbasic.domain.member.Member
import com.example.stresstestbasic.domain.member.MemberInfoUpdateRequest
import com.example.stresstestbasic.domain.member.MemberRegisterRequest
import jakarta.validation.Valid

interface MemberRegister {
    fun register(@Valid registerRequest: MemberRegisterRequest): Member
    fun activate(memberId: Long): Member
    fun deactivate(memberId: Long): Member
    fun updateInfo(memberId: Long, memberInfoUpdateRequest: @Valid MemberInfoUpdateRequest): Member
}