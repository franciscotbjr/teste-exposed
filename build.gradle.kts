val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val cliktVersion: String by project
val sqliteVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.1.21"

}

group = "org.hexasilith"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")

    implementation("ch.qos.logback:logback-classic:${logbackVersion}")

    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    implementation("org.xerial:sqlite-jdbc:${sqliteVersion}")
    implementation("com.zaxxer:HikariCP:${hikariVersion}")

    implementation("com.github.ajalt.clikt:clikt:${cliktVersion}")
    implementation("com.github.ajalt.clikt:clikt-markdown:${cliktVersion}")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar.configure {
    manifest {
        attributes(mapOf("Main-Class" to "org.hexasilith.MainKt"))
    }
    configurations["compileClasspath"].forEach {
            file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}