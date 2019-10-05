import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
}

group = "dev.mahabal.runetech"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // ASM is used to perform the bytecode analysis on the gamepack to determine revision
    // https://mvnrepository.com/artifact/org.ow2.asm/asm
    implementation("org.ow2.asm:asm-tree:7.2")
    // JSoup is used to easily connect and download web content
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.12.1")
    // Clikt is used out of laziness to provide simple and easy to configure CLI argument parsing
    // https://mvnrepository.com/artifact/com.github.ajalt/clikt
    implementation("com.github.ajalt:clikt:2.2.0")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
    testCompile("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}


tasks.jar {
    enabled = false
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val fatJar = task("fatJar", type = Jar::class) {
    archiveName = "${project.name}.jar"
    manifest {
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "dev.mahabal.runetech.GamepackDownloaderKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}