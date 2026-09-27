plugins {
    kotlin("jvm") version "1.9.25"
    application
}

group = "cl.petcare"
version = "1.0"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("cl.petcare.MainKt")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.release.set(21)
}

tasks.test {
    useJUnitPlatform()
}
