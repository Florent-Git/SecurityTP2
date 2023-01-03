plugins {
    kotlin("jvm") version "1.7.20"
    // Add the compose plugin
    id("org.jetbrains.compose") version "1.2.2"
}

group = "be.rm.secu.tp2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(mapOf("path" to ":core")))
    testImplementation(kotlin("test"))

    implementation(platform("io.projectreactor:reactor-bom:2022.0.1"))
    implementation ("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation ("io.projectreactor.netty:reactor-netty-core")

    // Add the coroutine reactor adapter
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")

    // Add the compose desktop UI
    implementation(compose.desktop.currentOs)

    // Add the kotlinx datetime dependency
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.20")
}

compose.desktop {
    application {
        mainClass = "be.rm.secu.tp2.acs-client.ApplicationKt"
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
