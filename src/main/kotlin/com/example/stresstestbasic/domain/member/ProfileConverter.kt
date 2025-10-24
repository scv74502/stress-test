package com.example.stresstestbasic.domain.member

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ProfileConverter : AttributeConverter<Profile, String> {

    override fun convertToDatabaseColumn(attribute: Profile?): String? {
        return attribute?.address
    }

    override fun convertToEntityAttribute(dbData: String?): Profile? {
        return dbData?.let { Profile(it) }
    }
}
