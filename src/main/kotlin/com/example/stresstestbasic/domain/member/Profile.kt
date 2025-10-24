package com.example.stresstestbasic.domain.member

import java.util.regex.Pattern

data class Profile(val address: String) {
    fun url(): String {
        return "@" + address
    }

    init {
        require(
            !(address == null || (!address.isEmpty() && !PROFILE_ADDRESS_PATTERN.matcher(
                address
            ).matches()))
        ) { "프로필 주소 형식이 바르지 않습니다: $address" }

        require(address.length <= 15) { "프로필 주소는 최대 15자리를 넘을 수 없습니다" }
    }

    companion object {
        private val PROFILE_ADDRESS_PATTERN: Pattern = Pattern.compile("[a-z0-9]+")
    }
}

