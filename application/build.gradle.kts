import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems.jar

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val space_sdk_version: String by project
val jackson_version: String by project
val exposed_version: String by project
val h2_version: String by project


plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("io.ktor.plugin") version "2.1.2"
}

group = "io.github.tscholze.spaces.onthisday"
version = "0.0.1"
application {
    mainClass.set("io.github.tscholze.onthisday.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/space/maven")
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Db
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")

    // Spaces SDK
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("org.jetbrains:space-sdk-jvm:$space_sdk_version")
}
