package com.example.stresstestbasic.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.proxy.HibernateProxy

@MappedSuperclass
abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) (this as HibernateProxy).getHibernateLazyInitializer()
            .persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        val that = other as AbstractEntity
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return if (this is HibernateProxy) (this as HibernateProxy).hibernateLazyInitializer.getPersistentClass()
            .hashCode() else javaClass.hashCode()
    }
}