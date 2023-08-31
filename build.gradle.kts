import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    java
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

// create sourceSet
val sc = sourceSets.create("intTest") {
    // compileClasspath and runtimeClasspath inherits depends on main's output, so that in the test of
    // integration test directory we can import classes from model or other packages of the main directory
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}
// creating a test type of task called integrationTest
val integrationTest = task<Test>("intTest") {
    if (System.getProperty("pactPublishResults") == "true") {
        systemProperty("pact.provider.version", getGitHash())
        systemProperty("pact.provider.tag", getGitBranch())
        systemProperty("pact.verifier.publishResults", "true")
    }


    description = "Runs integration tests."
    group = "verification"

    testClassesDirs += sourceSets["intTest"].output.classesDirs
    testClassesDirs += sourceSets["main"].output.classesDirs
    classpath += sourceSets["intTest"].runtimeClasspath
    classpath += sourceSets["main"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val intTestRuntimeOnly by configurations.getting

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    intTestImplementation("org.springframework.boot:spring-boot-starter-test")
    intTestImplementation("org.springframework.boot:spring-boot-starter-web")
    intTestImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
}
