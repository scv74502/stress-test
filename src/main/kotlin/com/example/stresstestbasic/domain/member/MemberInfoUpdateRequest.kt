package com.example.stresstestbasic.domain.member

import jakarta.validation.constraints.Size

data class MemberInfoUpdateRequest(
    val nickname: @Size(min = 5, max = 20) String,
    val profileAddress: @Size(max = 15) String,
    val introduction: String
)