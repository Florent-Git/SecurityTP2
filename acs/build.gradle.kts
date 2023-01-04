val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
    application
}

group = "be.rm.secu.tp2"
version = "0.0.1"
application {
    mainClass.set("be.rm.secu.tp2.acs.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.projectreactor:reactor-bom:2022.0.1"))
    implementation ("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation ("io.projectreactor.netty:reactor-netty-core")

    // Add the reactor coroutines adapter
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation(project(":core"))

    implementation("dev.turingcomplete:kotlin-onetimepassword:2.4.0")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

// Specify JVM target version
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

// Set standard input
tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}
