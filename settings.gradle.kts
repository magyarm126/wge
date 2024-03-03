pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.21"
        id("com.google.devtools.ksp") version "1.9.21-1.0.16"
        id("io.micronaut.library") version "4.2.0"
        id("org.jetbrains.kotlin.plugin.allopen") version "1.9.21"
        id("org.jetbrains.dokka") version "1.9.10"
        id("com.github.johnrengelman.shadow") version "8.1.1"
        id("io.micronaut.application") version "4.2.0"
        id("io.micronaut.aot") version "4.2.0"
    }
}

rootProject.name="wge"

include("backend")
project(":backend").projectDir = file("web/backend")

include("frontend")
project(":frontend").projectDir = file("web/frontend")

include("middleware")
project(":middleware").projectDir = file("web/middleware")