val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val cliktVersion: String by project
val sqliteVersion: String by project
val typesafeVersion: String by project
val fusesourceVersion: String by project
val slf4jVersion: String by project
val flywayVersion: String by project
val jupiterVersion: String by project
val mockitoVersion: String by project
val coroutinesTestVersion: String by project
val ktorMockVersion: String by project
val javafxVersion: String by project
val coroutineVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
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

    //implementation("ch.qos.logback:logback-classic:${logbackVersion}")
    implementation("org.slf4j:slf4j-nop:${slf4jVersion}")

    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    implementation("org.xerial:sqlite-jdbc:${sqliteVersion}")
    implementation("com.zaxxer:HikariCP:${hikariVersion}")

    // Flyway dependencies
    implementation("org.flywaydb:flyway-core:${flywayVersion}")

    implementation("com.github.ajalt.clikt:clikt:${cliktVersion}")
    implementation("com.github.ajalt.clikt:clikt-markdown:${cliktVersion}")

    implementation("com.typesafe:config:${typesafeVersion}")

    implementation("org.fusesource.jansi:jansi:${fusesourceVersion}")

    // JavaFX dependencies
    implementation("org.openjfx:javafx-controls:${javafxVersion}")
    implementation("org.openjfx:javafx-fxml:${javafxVersion}")
    implementation("org.openjfx:javafx-base:${javafxVersion}")
    implementation("org.openjfx:javafx-graphics:${javafxVersion}")

    // Kotlinx Coroutines JavaFX
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:${coroutineVersion}")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:${jupiterVersion}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${mockitoVersion}")
    testImplementation("org.mockito:mockito-inline:${mockitoVersion}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${coroutinesTestVersion}")
    testImplementation("io.ktor:ktor-client-mock:${ktorMockVersion}")
}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("org.hexasilith.MainKt")
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
        attributes["Multi-Release"] = "true"
    }
    configurations["compileClasspath"].forEach {
            file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}