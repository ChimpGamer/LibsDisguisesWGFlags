import java.util.*

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "nl.chimpgamer.libsdisguiseswgflags"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")

    // WorldGuard repo
    maven("https://maven.enginehub.org/repo/")

    // ProtocolLib repo
    maven("https://repo.dmulloy2.net/repository/public/")

    // Lib's Disguise repo
    maven("https://repo.md-5.net/content/groups/public/")
}

dependencies {
    compileOnly(kotlin("stdlib"))

    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8") // WorldGuard

    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0") // ProtocolLib

    compileOnly("LibsDisguises:LibsDisguises:10.0.37") // LibsDisguises
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-v${project.version}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}

fun String.capitalizeWords() = split("[ _]".toRegex()).joinToString(" ") { s -> s.lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
