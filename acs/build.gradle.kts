val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    `kotlin-conventions`
}

group = "be.rm.secu.tp2"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}