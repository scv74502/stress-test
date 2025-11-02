package com.example.stresstestbasic.domain.shared

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EmailConverter : AttributeConverter<Email, String> {

    override fun convertToDatabaseColumn(attribute: Email?): String? {
        return attribute?.email
    }

    override fun convertToEntityAttribute(dbData: String?): Email? {
        return dbData?.let { Email(it) }
    }
}