package com.example.stresstestbasic.domain.member

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class MemberRegisterRequest(
    val email: @Email String,
    val nickname: @Size(min = 5, max = 20) String,
    val password: @Size(min = 8, max = 100) String
)