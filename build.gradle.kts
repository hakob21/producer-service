import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.hakob"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("au.com.dius.pact.consumer:kotlin:4.6.1")

    // xz
//    implementation("au.com.dius.pact:provider:4.6.1")

    // will need these maybe
//    testImplementation("au.com.dius.pact.provider:junit5:4.6.1")
//    testImplementation("au.com.dius.pact.provider:spring:4.6.1")

    implementation("au.com.dius.pact.provider:spring6:4.6.2")

}

val getGitBranch = {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

val getGitHash = {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

tasks {
    named<Test>("test") {
        useJUnitPlatform()

        if (System.getProperty("pactPublishResults") == "true") {
            systemProperty("pact.provider.version", getGitHash())
            systemProperty("pact.provider.tag", getGitBranch())
            systemProperty("pact.verifier.publishResults", "true")
        }
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
