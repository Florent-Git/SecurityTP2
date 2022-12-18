plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "be.rm.secu.tp2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(mapOf("path" to ":core")))
    testImplementation(kotlin("test"))

    implementation(platform("io.projectreactor:reactor-bom:2022.0.1"))
    implementation ("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation ("io.projectreactor.netty:reactor-netty-core")

    // Add the coroutine reactor adapter
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.20")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("be.rm.secu.tp2.acs-client.ApplicationKt")
}
