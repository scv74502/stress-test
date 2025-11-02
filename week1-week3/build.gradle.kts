import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    // KSP annotation processor
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// kotlin 컴파일 시 옵션
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

// junit 테스트 시 사용하는 설정
tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

val queryDslVersion = "7.0"

dependencies {
    // 코틀린에 필요한 기존 의존성
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")   // validation

    // JPA 및 DB 연결 설정
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    // QueryDSL
    implementation("io.github.openfeign.querydsl:querydsl-jpa:${queryDslVersion}")

    // KSP를 사용한 querydsl 어노테이션 프로세서 추가
    ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:${queryDslVersion}")

    // .env 파일 지원
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}