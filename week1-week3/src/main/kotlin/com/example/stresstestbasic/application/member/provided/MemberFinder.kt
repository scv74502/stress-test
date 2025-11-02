package com.example.stresstestbasic.application.member.provided

import com.example.stresstestbasic.domain.member.Member

interface MemberFinder {
    fun find(memberId: Long): Member
}