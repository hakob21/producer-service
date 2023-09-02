import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("au.com.dius.pact") version "4.6.2"
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
val sc = sourceSets.create("pactTest") {
    // compileClasspath and runtimeClasspath inherits depends on main's output, so that in the test of
    // integration test directory we can import classes from model or other packages of the main directory
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}
// creating a test type of task called integrationTest
val pactTest = task<Test>("pactTest") {
    if (System.getProperty("pactPublishResults") == "true") {
        systemProperty("pact.provider.version", getGitHash())
        systemProperty("pact.provider.tag", getGitBranch())
        systemProperty("pact.verifier.publishResults", "true")
    }


    description = "Runs integration tests."
    group = "verification"

    testClassesDirs += sourceSets["pactTest"].output.classesDirs
    testClassesDirs += sourceSets["main"].output.classesDirs
    classpath += sourceSets["pactTest"].runtimeClasspath
    classpath += sourceSets["main"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}

val pactTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val pactTestRuntimeOnly by configurations.getting

configurations["pactTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    pactTestImplementation("org.springframework.boot:spring-boot-starter-test")
    pactTestImplementation("org.springframework.boot:spring-boot-starter-web")
    pactTestImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
}


pact {
    broker {
        pactBrokerUrl = "http://16.171.86.61/"

        // To use basic auth
//        pactBrokerUsername = '<USERNAME>'
//        pactBrokerPassword = '<PASSWORD>'

        // OR to use a bearer token
//        pactBrokerToken = '<TOKEN>'

        // Customise the authentication header from the default `Authorization`
//        pactBrokerAuthenticationHeader = 'my-auth-header'
    }

}
