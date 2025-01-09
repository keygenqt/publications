
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    fatJar {
        archiveFileName.set("telegram-bot.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.html.builder)

    // DB
    implementation(libs.postgresql)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)

    // Migration
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)

    // Other
    implementation(libs.picocli)
    implementation(libs.telegrambots)
    implementation(libs.kotlinx.html)
    implementation(libs.koin.core)
    implementation(libs.logback.classic)
}
