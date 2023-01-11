val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.7.22"
    application
}

group = "be.rm.secu.tp2"
version = "0.0.1"

application {
    mainClass.set("be.rm.secu.tp2.acq.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.projectreactor:reactor-bom:2022.0.1"))
    implementation ("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation ("io.projectreactor.netty:reactor-netty-core")

    // Add the coroutine reactor adapter
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation(project(":core"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}
