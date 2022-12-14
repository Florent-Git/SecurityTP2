plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {

}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
