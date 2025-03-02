plugins {
    val plugins = libs.plugins

    alias(plugins.kotlin.jvm)
    alias(plugins.kotlin.spring)
    alias(plugins.springframework.boot)
    alias(plugins.spring.dependency.management)
    alias(plugins.ktlint)
}

group = "com.insuk"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.kotlin.exposed)

    runtimeOnly(libs.h2database)

    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.fixture.monkey.test)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
