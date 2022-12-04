plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.test {
    useJUnitPlatform()
}
