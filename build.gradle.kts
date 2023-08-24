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

// https://docs.gradle.org/current/userguide/java_testing.html#sec:configuring_java_integration_tests
tasks.withType<Test> {
//    enabled = false
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
    description = "Runs integration tests."
    group = "verification"

    // the built test classes directories in "build" directory are added to the testClassesDirs of this newly created task
    // the "sourceSets["intTest"].output.classesDirs" returns
    // /Users/hakobharutyunyan/projects/pact/provider-service/build/classes/java/intTest
    // /Users/hakobharutyunyan/projects/pact/provider-service/build/classes/kotlin/intTest
    testClassesDirs += sourceSets["intTest"].output.classesDirs
    testClassesDirs += sourceSets["main"].output.classesDirs

    // the compiled jars of dependencies (libraries) and the following directories are added to the classpath
    // of the newly created integration test task
    // /Users/hakobharutyunyan/projects/pact/provider-service/build/classes/java/intTest
    // /Users/hakobharutyunyan/projects/pact/provider-service/build/classes/kotlin/intTest
    // /Users/hakobharutyunyan/projects/pact/provider-service/build/resources/intTest
    classpath += sourceSets["intTest"].runtimeClasspath
    classpath += sourceSets["main"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}
//tasks.check { dependsOn(integrationTest) }

// we extend from the "implementation" so that we can use "intTestImplementation"
// below this to define dependencies for the new source set
val intTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val intTestRuntimeOnly by configurations.getting

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

// for some reason according to that tutorial we need to include dependencies for this newly created source set
// at n26 we don't do this
dependencies {
    intTestImplementation("org.springframework.boot:spring-boot-starter-test")
    intTestImplementation("org.springframework.boot:spring-boot-starter-web")
    intTestImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
//    intTestRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks.register("printSourceSetInformation") {
    doLast {
//        sourceSets["main"].runtimeClasspath.forEach { println(it) }
//        sourceSets["main"].compileClasspath.forEach { println(it) }

        sourceSets["test"].runtimeClasspath.forEach { println(it) }
//        sourceSets["intTest"].runtimeClasspath.forEach { println(it) }
//        sourceSets["main"].output.classesDirs.forEach { println(it) }
//        sourceSets.forEach { srcSet ->
//            println("[${srcSet.name}]")
//            println("-->Source directories: ${srcSet.allJava.srcDirs}")
//            println("-->Output directories: ${srcSet.output.classesDirs.files}")
//            println()
//        }
//        project.plugins.forEach {
//            println(it)
//        }
    }
}
