package com.example.stresstestbasic.application.member.required

import com.example.stresstestbasic.domain.member.Member
import com.example.stresstestbasic.domain.member.Profile
import com.example.stresstestbasic.domain.member.QMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class MemberQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val memberRepository: MemberRepository
): MemberQueryRepository {
    override fun findByProfile(profile: Profile): Member? {
        val result = queryFactory
            .select(QMember.member)
            .from(QMember.member)
            .where(QMember.member.detail.profileAddress.eq(profile.address))
            .fetchOne()

        return result
    }
}