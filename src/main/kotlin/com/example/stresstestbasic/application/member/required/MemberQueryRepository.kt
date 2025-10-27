package com.example.stresstestbasic.application.member.required

import com.example.stresstestbasic.domain.member.Member
import com.example.stresstestbasic.domain.member.Profile

interface MemberQueryRepository {
    fun findByProfile(profile: Profile): Member?
}